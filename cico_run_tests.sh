#!/bin/bash

set -ex

. cico_setup.sh

mvn clean verify

echo "Code coverage report BEGIN"
cd target/site/jacoco
cat jacoco.csv
echo "Code coverage report END"
