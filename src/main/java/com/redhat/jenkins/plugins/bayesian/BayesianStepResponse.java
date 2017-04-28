package com.redhat.jenkins.plugins.bayesian;

/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *limitations under the License.
 */

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
