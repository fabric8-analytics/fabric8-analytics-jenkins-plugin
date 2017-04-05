package com.redhat.jenkins.plugins.bayesian;

import java.io.Serializable;

import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;

public class BayesianStepResponse implements Serializable {

    private String token;
    private String content;
    private boolean success;
    private String analysisUrl;

    public BayesianStepResponse(String token, String content, String analysisUrl, boolean success) {
        super();
        this.token = token;
        this.content = content;
        this.success = success;
        this.analysisUrl = analysisUrl;
    }

    @Whitelisted
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Whitelisted
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Whitelisted
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Whitelisted
    public String getAnalysisUrl() {
        return analysisUrl;
    }

    public void setAnalysisUrl(String analysisUrl) {
        this.analysisUrl = analysisUrl;
    }

    public static BayesianStepResponse emptyResposnse() {
        return new BayesianStepResponse("", "", "", false);
    }

    private static final long serialVersionUID = 1L;
}
