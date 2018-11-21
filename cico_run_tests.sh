#!/bin/bash

set -ex

. cico_setup.sh

mvn clean verify

# don't be so talkative ;)
set +x

export CODECOV_TOKEN="1bf0c472-5093-437b-b586-919efed73ebf"
bash <(curl -s https://codecov.io/bash)

echo "Code coverage report BEGIN"
cd target/site/jacoco
cat jacoco.csv
echo "Code coverage report END"
