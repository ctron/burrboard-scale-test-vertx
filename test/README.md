```shell
podman run --rm -ti -p 8883:8883 --userns=keep-id -v $PWD:/mosquitto/config docker.io/library/eclipse-mosquitto:openssl mosquitto -c /mosquitto/config/mosquitto-tls.conf
```