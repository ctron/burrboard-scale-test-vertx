package io.drogue.cloud.burrboard.scale.test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class Device {
    private final String name;

    public Device(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Future<Void> tick(final GatewayContext ctx) {
        return ctx.sendAsJson(
                        this.name,
                        "state", MqttQoS.AT_MOST_ONCE,
                        mergeFeature("accelerometer",
                                new JsonObject()
                                        .put("x", 0)
                                        .put("y", 0)
                                        .put("z", 0)))
                .mapEmpty();
    }

    static JsonObject mergeFeature(final String feature, final JsonObject value) {
        final String path = String.format("/features/%s/properties", URLEncoder.encode(feature, StandardCharsets.UTF_8));

        return new JsonObject()
                .put("headers", new JsonObject()
                        .put("content-type", "application/merge-patch+json"))
                .put("path", path)
                .put("value", value);
    }
}
