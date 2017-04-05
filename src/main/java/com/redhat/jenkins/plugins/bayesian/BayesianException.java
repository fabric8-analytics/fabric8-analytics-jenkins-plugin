package com.redhat.jenkins.plugins.bayesian;

public class BayesianException extends Exception {

    public BayesianException(String message, Throwable cause) {
        super(message, cause);
    }

    public BayesianException(String message) {
        super(message);
    }

    public BayesianException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 1L;
}
