package io.drogue.cloud.burrboard.scale.test;

import java.util.Optional;
import java.util.function.Function;

public class Configuration {

    private final int numberOfGateways;
    private final int devicesPerGateway;

    private String host;
    private int port;
    private boolean tls;
    private boolean insecure;

    private String application;
    private String gatewayDevice;
    private String password;

    private Configuration() {
        this.numberOfGateways = numberOfGateways();
        this.devicesPerGateway = devicesPerGateway();
    }

    public int getPort() {
        return this.port;
    }

    public String getHost() {
        return this.host;
    }

    public boolean isTls() {
        return this.tls;
    }

    public boolean isInsecure() {
        return this.insecure;
    }

    public String getApplication() {
        return this.application;
    }

    public String getGatewayDevice() {
        return this.gatewayDevice;
    }

    public String getFullGatewayDevice() {
        return String.format("%s@%s", this.gatewayDevice, this.application);
    }

    public String getPassword() {
        return this.password;
    }

    public int getNumberOfGateways() {
        return this.numberOfGateways;
    }

    public int getDevicesPerGateway() {
        return this.devicesPerGateway;
    }

    public static Configuration devbox() {
        var config = new Configuration();
        config.host = "mqtt-endpoint-drogue-dev.apps.wonderful.iot-playground.org";
        config.port = 443;
        config.tls = true;
        config.application = "burrboard";
        config.gatewayDevice = System.getenv("GATEWAY");
        config.password = System.getenv("PASSWORD");
        return config;
    }

    public static Configuration devboxLoadBalancer() {
        var config = devbox();
        config.host = "mqtt-endpoint-drogue-dev.apps.wonderful.iot-playground.org";
        config.port = 8884;
        return config;
    }

    public static Configuration local() {
        var config = new Configuration();
        config.host = "localhost";
        config.port = 1883;
        return config;
    }

    public static Configuration localTls() {
        var config = new Configuration();
        config.host = "localhost";
        config.port = 8883;
        config.tls = true;
        config.insecure = true;
        return config;
    }

    private static <T> Optional<T> env(String name, Function<String, T> converter) {
        return Optional
                .ofNullable(System.getenv(name))
                .map(converter);
    }

    private static int numberOfGateways() {
        return env("NUMBER_OF_GATEWAYS", Integer::parseInt).orElse(1);
    }

    private static int devicesPerGateway() {
        return env("DEVICES_PER_GATEWAY", Integer::parseInt).orElse(50);
    }
}
