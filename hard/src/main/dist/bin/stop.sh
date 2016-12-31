#!/usr/bin/env bash

export JAVA_OPTS=-Djava.library.path=/usr/lib/jni

cd /home/debian/vertx3/hard-1.0-SNAPSHOT
./bin/hard stop domohard