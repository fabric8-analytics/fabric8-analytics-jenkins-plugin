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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;


public final class BayesianAnalysisStep extends Step {

    private String url;
    private String gitUrl;
    private String ecosystem;

    @DataBoundConstructor
    public BayesianAnalysisStep() {
    }

    @DataBoundSetter
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
    
    @DataBoundSetter
    public void setGitUrl(String gitUrl) {
    	if(! gitUrl.endsWith(".git")) {
    		gitUrl = gitUrl + ".git";
    	}
        this.gitUrl = gitUrl;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    @DataBoundSetter
    public void setEcosystem(String ecosystem) {
        this.ecosystem = ecosystem;
    }

    public String getEcosystem() {
        return ecosystem;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(this, context);
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<BayesianStepResponse> {

        private transient final BayesianAnalysisStep step;

        protected Execution(BayesianAnalysisStep step, StepContext context) {
            super(context);
            this.step = step;
        }

        @Override
        protected BayesianStepResponse run() throws Exception {

            PrintStream logger = getContext().get(TaskListener.class).getLogger();

            List<FilePath> manifests = Utils.findManifests(getContext().get(FilePath.class));
            List<FilePath> deps = Utils.findDependencies(getContext().get(FilePath.class));

            BayesianStepResponse emptyResponse = BayesianStepResponse.emptyResposnse();

            if (manifests.isEmpty()) {
                logger.println("No supported manifest files found.");
            }
            
            if (deps.isEmpty()) {
                logger.println("No direct or transitive dependencies found.");
            }
            else {
                logger.println("Found dependencies.");
            }            

            // TODO: refactor
            String url = (step.getUrl() != null) ? step.getUrl() : Bayesian.getDefaultUrl();
            String gitUrl = (step.getGitUrl() != null) ? step.getGitUrl() : "";
            Bayesian bayesian = new Bayesian(url,gitUrl,step.getEcosystem());

            BayesianStepResponse response = null;
            try {
                logger.println("Running Bayesian stack analysis...");
                logger.println("Bayesian API URL is " + bayesian.getApiUrl());
                logger.println("Ecosystem is " + bayesian.getEcosystem());
                logger.println("Git URL is " + bayesian.getGitUrl());
                response = bayesian.submitStackForAnalysis(manifests,deps);
            } catch (Throwable e) {
                // intentionally not failing the build here
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                logger.println(sw.toString());
            } finally {
                // just to make sure
                bayesian = null;
            }

            if (response == null) {
                response = emptyResponse;
            }

            return response;
        }

        private static final long serialVersionUID = 1L;
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "bayesianAnalysis";
        }

        @Override
        public String getDisplayName() {
            return "Bayesian Analysis";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.unmodifiableSet(new HashSet<Class<?>>(Arrays.asList(FilePath.class, TaskListener.class)));
        }
    }
}
