#!/bin/bash

set -ex

. cico_setup.sh

mvn clean verify

cd target/site/jacoco
ls -1
