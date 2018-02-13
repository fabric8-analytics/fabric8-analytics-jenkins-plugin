#!/bin/bash -ex

load_jenkins_vars() {
    if [ -e "jenkins-env" ]; then
        cat jenkins-env \
          | grep -E "(JENKINS_URL|GIT_BRANCH|GIT_COMMIT|BUILD_NUMBER|ghprbSourceBranch|ghprbActualCommit|BUILD_URL|ghprbPullId)=" \
          | sed 's/^/export /g' \
          > ~/.jenkins-env
        source ~/.jenkins-env
    fi
}

prep() {
    yum -y update
    yum -y install centos-release-scl-rh
    yum -y install java-1.8.0-openjdk rh-maven33
}

load_jenkins_vars
prep
. /opt/rh/rh-maven33/enable
