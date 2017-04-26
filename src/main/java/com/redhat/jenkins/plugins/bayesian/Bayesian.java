package com.redhat.jenkins.plugins.bayesian;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
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
import org.apache.http.protocol.HTTP;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hudson.FilePath;


/* package */ class Bayesian {

    private static final String DEFAULT_BAYESIAN_URL = "https://recommender.api.openshift.io/api/v1";
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
            cnames = null;
        }
        this.url = uri.toString();
    }

    public BayesianStepResponse submitStackForAnalysis(Collection<FilePath> manifests) throws BayesianException {
        String stackAnalysesUrl = getApiUrl() + "/stack-analyses";
        HttpPost httpPost = new HttpPost(stackAnalysesUrl);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (FilePath manifest : manifests) {
            byte[] content = null;
            try (InputStream in = manifest.read()) {
                content = ByteStreams.toByteArray(in);
                builder.addBinaryBody("manifest[]", content, ContentType.DEFAULT_BINARY, manifest.getName());
            } catch (IOException | InterruptedException e) {
                throw new BayesianException(e);
            } finally {
                content = null;
            }
        }
        HttpEntity multipart = builder.build();
        builder = null;
        httpPost.setEntity(multipart);
        httpPost.setHeader("Authorization", "Bearer " + getAuthToken());

        BayesianResponse responseObj = null;
        Gson gson;
        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            // Yeah, the endpoint actually returns 200 from some reason;
            // I wonder what happened to the good old-fashioned 202 :)
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new BayesianException("Bayesian error: " + response.getStatusLine().getStatusCode());
            }

            Charset charset = ContentType.get(entity).getCharset();
            try (InputStream is = entity.getContent(); Reader reader = new InputStreamReader(is, charset != null ? charset : HTTP.DEF_CONTENT_CHARSET)) {
                gson = new GsonBuilder().create();
                responseObj = gson.fromJson(reader, BayesianResponse.class);
                String analysisUrl = stackAnalysesUrl + "/" + responseObj.getId();
                return new BayesianStepResponse(responseObj.getId(), "", analysisUrl, true);
            }
        } catch (IOException e) {
            throw new BayesianException("Bayesian error", e);
        } finally {
            // just to be sure...
            responseObj = null;
            httpPost = null;
            multipart = null;
            gson = null;
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

    private String getAuthToken() {
        return System.getProperty("RECOMMENDER_API_TOKEN", "token-not-available-in-pipelines");
    }
}
