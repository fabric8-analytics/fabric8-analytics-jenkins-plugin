package bayesian;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class BayesianTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void bayesianAnalysisStepTest() throws Exception {

        String analysisId = "421249d9e1e5464cbf3e77dde4941463";

        String apiResponse =
                "{\"id\": \"" + analysisId + "\",\n" +
                "\"status\": \"success\",\n" +
                "\"submitted_at\": \"2018-10-20 05:09:01.165068\"}";

        String keycloakApiResponse =
                "{\"data\": [{\"attributes\": {\"email\": \"test@example.com\"}}]}";

        // Mock the API call
        stubFor(post(urlEqualTo("/api/v1/stack-analyses"))
                .withHeader("Authorization", containing("Bearer "))
                .withMultipartRequestBody(aMultipart().withName("manifest[]").withName("filePath[]"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(apiResponse)));

        // Mock the KeyCloak API call
        stubFor(get(urlEqualTo("/api/users?filter[username]=test"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(keycloakApiResponse)));

        // Configure the build
        WorkflowJob proj = j.jenkins.createProject(WorkflowJob.class, "proj");
        proj.setDefinition(new CpsFlowDefinition(
            "node() { \n"
            + "writeFile text: '<project></project>', file: 'pom.xml' \n"
            + "writeFile text: '<project></project>', file: 'target/stackinfo/poms/pom.xml' \n"
            + "def response = bayesianAnalysis url: 'http://localhost:8080' \n"
            + "print(\"Analysis ID is \" + response.getToken()) \n"
            + "}",
            false));

        // Execute the build
        WorkflowRun run = proj.scheduleBuild2(0).get();

        // Check expectations
        j.assertBuildStatusSuccess(run);
        j.assertLogContains("Running Bayesian stack analysis", run);
        j.assertLogContains("Analysis ID is " + analysisId, run);
    }
}
