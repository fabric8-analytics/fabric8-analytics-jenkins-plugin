#!/bin/bash

set -ex

. cico_setup.sh

mvn clean verify

cd target
ls -1
