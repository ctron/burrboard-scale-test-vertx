package io.drogue.cloud.burrboard.scale.test;

import java.util.Optional;
import java.util.function.Function;

public class Configuration {

    private String host;
    private int port;
    private boolean tls;
    private boolean insecure;

    private String application;
    private String gatewayDevice;
    private String password;
    private int numberOfDevices;

    private Configuration() {
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

    public int getNumberOfDevices() {
        return this.numberOfDevices;
    }

    public static Configuration devbox() {
        var config = new Configuration();
        config.host = "mqtt-endpoint-drogue-dev.apps.wonderful.iot-playground.org";
        config.port = 443;
        config.tls = true;
        config.application = "ctron-test-ditto";
        config.gatewayDevice = System.getenv("GATEWAY");
        config.password = System.getenv("PASSWORD");
        config.numberOfDevices = env("NUMBER_OF_DEVICES", Integer::parseInt).orElse(100);
        return config;
    }

    private static <T> Optional<T> env(String name, Function<String, T> converter) {
        return Optional
                .ofNullable(System.getenv(name))
                .map(converter);
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
        config.numberOfDevices = 100;
        return config;
    }

    public static Configuration localTls() {
        var config = new Configuration();
        config.host = "localhost";
        config.port = 8883;
        config.tls = true;
        config.insecure = true;
        config.numberOfDevices = 100;
        return config;
    }
}
