## Set up devices

```shell

# create the gateway device
drg create device -a burrboard gateway01
drg set password -a burrboard foobargw01 gateway01

# create the actual devices
for i in $(seq -w 00000 01000); do
  drg create device -a burrboard simulated$i
done

# assign the gateway
for i in $(seq -w 00000 01000); do
  drg set gateway -a burrboard simulated$i gateway01
done

```