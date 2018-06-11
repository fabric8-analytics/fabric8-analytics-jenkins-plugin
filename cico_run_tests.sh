#!/bin/bash

set -ex

. cico_setup.sh

mvn clean verify

ls -1
tree
