package io.drogue.cloud.burrboard.scale.test;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public interface GatewayContext {
    Future<Integer> sendAs(String device, String subject, MqttQoS qos, Buffer payload);

    default Future<Integer> sendAsJson(String device, String subject, MqttQoS qos, JsonObject payload) {
        return sendAs(device, subject, qos, payload.toBuffer());
    }

}
