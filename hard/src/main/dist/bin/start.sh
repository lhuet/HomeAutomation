#!/usr/bin/env bash

# Load DeviceTree beaglebone "module"
# echo BB-W1 > /sys/devices/bone_capemgr.8/slots
# echo BB-UART1 > /sys/devices/bone_capemgr.8/slots

# JNI library installed with apt-get install rxtx
export JAVA_OPTS=-Djava.library.path=/usr/lib/jni

cd /home/debian/vertx3/hard-1.0-SNAPSHOT
./bin/hard start --cluster --conf ../conf.json --vertx-id domohard fr.lhuet.home.hardware.MainVerticle

