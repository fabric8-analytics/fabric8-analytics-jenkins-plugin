# Fabric8-Analytics Jenkins plugin

A very simple Jenkins plugin which can automatically submit supported manifest files
to fabric8-analytics for analysis.

*Note on naming: The Fabric8-Analytics project has evolved from 2 different projects called "cucos" and "bayesian". We're currently in process of renaming the modules and updating documentation. Until that is completed, please consider "cucos" and "bayesian" to be synonyms of "Fabric8-Analytics".*

## Contributing

See our [contributing guidelines](https://github.com/fabric8-analytics/fabric8-analytics-common/blob/master/CONTRIBUTING.md) for more info.

## Configuration

The plugin is very simple at this point and you only need to activate it
in jobs for which you want to trigger the scan. Only pipeline jobs
are supported at the moment.


### Pipeline

Simply add another step to your pipeline:

```
def response = bayesianAnalysis url: 'https://<fabric8-analytics-recommender-hostname>', gitUrl: <git repo URL>
echo("The results will be available at " + response.analysisUrl)
```

