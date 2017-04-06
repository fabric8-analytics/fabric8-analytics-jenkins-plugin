package com.redhat.jenkins.plugins.bayesian;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;

import hudson.FilePath;


/* package */ class Bayesian {

    private static final String DEFAULT_BAYESIAN_URL = "https://recommender.api.prod-preview.openshift.io";
    private String url;

    public Bayesian() throws URISyntaxException {
        this(DEFAULT_BAYESIAN_URL);
    }

    public Bayesian(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String host = uri.getHost();
        if (host.indexOf('.') == -1) {
            // looks like it's a short domain name
            // TODO: there can be dots in short domain names as well
            List<String> cnames = DnsFiddler.getActualCNAME(host);
            if (!cnames.isEmpty()) {
                String hostname = cnames.get(0);
                if (hostname.endsWith(".")) {
                    hostname = hostname.substring(0, hostname.length() - 1);
                }
                uri = new URIBuilder(uri).setHost(hostname).build();
            }
        }
        this.url = uri.toString();
    }

    public BayesianStepResponse submitStackForAnalysis(Collection<FilePath> manifests) throws BayesianException {
        String stackAnalysesUrl = getApiUrl() + "/stack-analyses";
        HttpPost httpPost = new HttpPost(stackAnalysesUrl);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (FilePath manifest : manifests) {
            try (InputStream in = manifest.read()) {
                byte[] content = ByteStreams.toByteArray(in);
                builder.addBinaryBody("manifest[]", content, ContentType.DEFAULT_BINARY, manifest.getName());
            } catch (IOException | InterruptedException e) {
                throw new BayesianException(e);
            }
        }
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        try (CloseableHttpClient client = HttpClients.createDefault();) {
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseContent = entity != null ? EntityUtils.toString(entity) : null;
            // Yeah, the endpoint actually returns 200 from some reason;
            // I wonder what happened to the good old-fashioned 202 :)
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new BayesianException("Bayesian error: " + responseContent);
            }

            ObjectMapper mapper = new ObjectMapper();
            BayesianResponse responseObj = mapper.readValue(responseContent, BayesianResponse.class);

            String analysisUrl = stackAnalysesUrl + "/" + responseObj.getId();
            return new BayesianStepResponse(responseObj.getId(), responseContent, analysisUrl, true);
        } catch (IOException e) {
            throw new BayesianException("Bayesian error", e);
        }
    }

    public String getApiUrl() {
        URIBuilder url;
        URI apiUrl = null;
        try {
            url = new URIBuilder(getUrl());
            apiUrl = url.setPath(url.getPath() + "/api/v1").build().normalize();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Bayesian URL is invalid.");
        }
        return apiUrl.toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static String getDefaultUrl() {
        return DEFAULT_BAYESIAN_URL;
    }
}
