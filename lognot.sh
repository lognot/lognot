#!/bin/bash

JAR=target/lognot-0.0.1-SNAPSHOT.jar

usage() {
    echo "Usage: $0 [-f <config_file>]" 1>&2
    exit 1
}

with_config=""
while getopts ":f:" o; do
    case "${o}" in
       f)
           with_config="--spring.config.location=file:${OPTARG}"
           ;;
       *)
           usage
           ;;
    esac
done
shift $((OPTIND-1))

echo "with config: $with_config"

java \
    -Dcom.sun.management.jmxremote.port=9999 \
    -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -jar $JAR \
    $with_config
