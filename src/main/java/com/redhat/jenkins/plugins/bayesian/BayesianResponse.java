package com.redhat.jenkins.plugins.bayesian;

class BayesianResponse {

    private String id;
    private String submitted_at;
    private String status;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSubmitted_at() {
        return submitted_at;
    }
    public void setSubmitted_at(String submitted_at) {
        this.submitted_at = submitted_at;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
