# fabric8-analytics Jenkins plugin

A very simple Jenkins plugin which can automatically submit supported manifest files
to fabric8-analytics for analysis.


## Configuration

The plugin is very simple at this point and you only need to activate it
in jobs for which you want to trigger the scan. Only pipeline jobs
are supported at the moment.


### Pipeline

Simply add another step to your pipeline:

```
def response = bayesianAnalysis url: 'https://<fabric8-analytics-recommender-hostname>'
echo("The results will be available at " + response.analysisUrl)
```

