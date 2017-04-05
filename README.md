# Bayesian Jenkins plugin

A very simple Jenkins plugin which can automatically submit supported manifest files
to Bayesian for analysis.

This document describes how to install and configure the plugin. Development related
information can be found in a separate [document](./development.md).


## How to get the plugin

You can either build the plugin yourself, or you can grab the
[latest build](https://cucos-jenkins.rhev-ci-vms.eng.rdu2.redhat.com/job/Bayesian_Project/job/bayesian-jenkins-plugin/lastSuccessfulBuild/artifact/target/bayesian.hpi) from our Jenkins.


## Configuration

The plugin is very simple at this point and you only need to activate it
in jobs for which you want to trigger Bayesian scan. Only pipeline jobs
are supported at the moment.


### Pipeline

Simply add another step to your pipeline:

```
def response = bayesianAnalysis()
echo(response.token)
```

