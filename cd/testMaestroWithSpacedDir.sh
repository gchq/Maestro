#!/usr/bin/env bash

set -e

mkdir "/tmp/maestro test/"
cp -R ./* "/tmp/maestro test/"
cd "/tmp/maestro test"
mvn package -Ptest
cd -
rm -rf "/tmp/maestro test/"
