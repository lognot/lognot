#!/bin/bash

java \
    -Dcom.sun.management.jmxremote.port=9999 \
    -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -jar target/lognot-0.0.1-SNAPSHOT.jar
