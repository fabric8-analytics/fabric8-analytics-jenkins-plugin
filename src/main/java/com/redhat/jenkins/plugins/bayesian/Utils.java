package com.redhat.jenkins.plugins.bayesian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hudson.FilePath;

/* package */ class Utils {

    @SuppressWarnings("serial")
    private static List<String> knownNpmManifests = new ArrayList<String>() {
        {
            add("package.json");
            add("npm-shrinkwrap.json");
        }
    };

    @SuppressWarnings("serial")
    private static List<String> knownMavenManifests = new ArrayList<String>() {
        {
            add("pom.xml");
        }
    };

    public static List<FilePath> findManifests(FilePath workspace) {
        List<FilePath> manifests = new ArrayList<FilePath>();

        // Maven
        List<FilePath> mavenManifests = findManifestsFromList(workspace, knownMavenManifests);
        if (!mavenManifests.isEmpty()) {
            // TODO: get all poms
            FilePath pom = workspace.child("target/stackinfo/poms/pom.xml");
            if (manifestExists(pom)) {
                manifests.add(pom);
            }
        }

        // NPM
        List<FilePath> npmManifests = findManifestsFromList(workspace, knownNpmManifests);
        manifests.addAll(npmManifests);

        return manifests;
    }

    private static List<FilePath> findManifestsFromList(FilePath workspace, List<String> manifests) {
        List<FilePath> result = new ArrayList<FilePath>();

        for (String manifest: manifests) {
            FilePath manifestFile = workspace.child(manifest);
            if (manifestExists(manifestFile)) {
                result.add(manifestFile);
            }
        }
        return result;
    }

    private static boolean manifestExists(FilePath manifestFile) {
        try {
            if (manifestFile.exists()) {
                return true;
            }
        } catch (IOException | InterruptedException e) {
            // TODO log
        }
        return false;
    }
}
