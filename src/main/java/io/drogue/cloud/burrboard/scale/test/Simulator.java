package io.drogue.cloud.burrboard.scale.test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class Simulator extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(Simulator.class);

    private final MeterRegistry registry;
    private final Configuration config;

    private Gateway[] gateways;
    private Long dumper;
    private Instant lastNow;
    private double lastCounter;
    private double lastTicksCounter;

    public Simulator() {
        this.registry = new SimpleMeterRegistry();
        this.config = Configuration.devboxLoadBalancer();
    }

    @Override
    public void start(Promise<Void> startPromise) {
        this.dumper = this.vertx.setPeriodic(1000, x -> this.dumpStats());

        @SuppressWarnings("rawtypes")
        var futures = new ArrayList<Future>(this.config.getNumberOfGateways());
        var amount = this.config.getDevicesPerGateway();

        this.gateways = new Gateway[this.config.getNumberOfGateways()];
        for (int i = 0; i < this.config.getNumberOfGateways(); i++) {
            this.gateways[i] = new Gateway(getVertx(), this.config, this.registry, i * amount, amount);
            futures.add(this.gateways[i].start());
        }

        CompositeFuture.all(futures)
                .<Void>mapEmpty()
                .onComplete(startPromise);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        if (this.dumper != null) {
            this.vertx.cancelTimer(this.dumper);
            this.dumper = null;
        }

        if (this.gateways != null) {
            @SuppressWarnings("rawtypes")
            var futures = new ArrayList<Future>(this.config.getNumberOfGateways());
            for (var gateway : this.gateways) {
                futures.add(gateway.stop());
            }
            CompositeFuture.all(futures)
                    .<Void>mapEmpty()
                    .onComplete(stopPromise);
        } else {
            stopPromise.complete();
        }
    }

    private void dumpStats() {
        var timer = this.registry.get("tickDurationTimer").timer();
        var updates = this.registry.get("updates").counter().count();
        var ticks = timer.count();

        var now = Instant.now();

        if (this.lastNow != null) {
            var dt = (double) Duration.between(this.lastNow, now).toMillis();
            var diff = (updates - this.lastCounter);
            var ups = (diff / dt) * 1000.0;
            var tps = ((ticks - this.lastTicksCounter) / dt) * 1000.0;

            log.info(String.format("Stats - timer: %.2f ms, ticks/s: %.0f, updates/s: %.0f (%+.0f)", timer.mean(TimeUnit.MILLISECONDS), tps, ups, diff));
        }

        this.lastNow = now;
        this.lastCounter = updates;
        this.lastTicksCounter = ticks;
    }

}
