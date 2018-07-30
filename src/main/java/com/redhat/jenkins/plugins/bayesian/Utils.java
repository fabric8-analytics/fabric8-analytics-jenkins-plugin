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

import java.io.IOException;
import java.net.URISyntaxException;
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
    private static List<String> knownDependencies = new ArrayList<String>() {
        {
            add("direct-dependencies.txt");
            add("transitive-dependencies.txt");
        }
    };

    @SuppressWarnings("serial")
    private static List<String> knownPythonManifests = new ArrayList<String>() {
        {
            add("requirements.txt");
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
            FilePath rootPom = workspace.child("target/stackinfo/poms/pom.xml");
            if (manifestExists(rootPom)) {
                manifests.add(rootPom);
            }

            FilePath childPomDirsPath = workspace.child("target/stackinfo/poms");
            List<FilePath> childPomDirs = getSubdirs(childPomDirsPath);
            for (FilePath childPomDir : childPomDirs) {
                FilePath childPom = childPomDir.child("pom.xml");
                if (manifestExists(childPom)) {
                    manifests.add(childPom);
                }
            }
        }

        // NPM
        List<FilePath> npmManifests = findManifestsFromList(workspace, knownNpmManifests);
        manifests.addAll(npmManifests);

        // Python
        List<FilePath> pythonManifests = findManifestsFromList(workspace, knownPythonManifests);
        manifests.addAll(pythonManifests);       

        return manifests;
    }
    
    public static List<FilePath> findDependencies(FilePath workspace) {
     // Dependencies
        List<FilePath> dependencies = findManifestsFromList(workspace, knownDependencies);
        return dependencies;
    }

    private static List<FilePath> findManifestsFromList(FilePath workspace, List<String> manifests) {
        List<FilePath> result = new ArrayList<FilePath>();

        for (String manifest : manifests) {
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

    private static List<FilePath> getSubdirs(FilePath file) {
        List<FilePath> subdirs = new ArrayList<FilePath>();
        try {
            subdirs = file.listDirectories();
        } catch (IOException | InterruptedException e) {
            // TODO log
        }

        List<FilePath> deepSubdirs = new ArrayList<FilePath>();
        for (FilePath subdir : subdirs) {
            deepSubdirs.addAll(getSubdirs(subdir));
        }
        subdirs.addAll(deepSubdirs);
        return subdirs;
    }
}
