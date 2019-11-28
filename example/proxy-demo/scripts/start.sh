#!/usr/bin/env bash

# Run this script from the top level directory of this repository.
# Usage: ./example/proxy-demo/scripts/start.sh [any extra mvn command arguments, e.g -am to build all dependencies]
mvn clean install -pl :proxy-demo -Pproxy-demo,quick $@
