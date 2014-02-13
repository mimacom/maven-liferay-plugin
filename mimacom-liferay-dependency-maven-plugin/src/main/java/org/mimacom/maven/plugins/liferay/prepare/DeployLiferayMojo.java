package org.mimacom.maven.plugins.liferay.prepare;


/*
 * Copyright (c) 2014 mimacom a.g.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Deploy liferay artifacts to a maven repository.
 *
 * @author stni
 * @goal deployLiferay
 * @requiresProject false
 */
public class DeployLiferayMojo extends AbstractUploadLiferayMojo {
    /**
     * The ID of the repository to deploy to.
     *
     * @parameter expression="${repositoryId}"
     * @required
     */
    private String repositoryId;

    /**
     * The URL of the repository to deploy to.
     *
     * @parameter expression="${repositoryUrl}"
     * @required
     */
    private String repositoryUrl;

    /**
     * The repository layout.
     *
     * @parameter expression="${repositoryLayout}" default-value="default"
     */
    private String repositoryLayout;

    @Override
    protected void doUploadDependencies(String sourceGroupId, String sourceArtifactId, String groupId) {
        try {
            executeMojo(//
                    WAR_MAVENIZER_PLUGIN,//
                    goal("deploy"),//
                    configuration(//
                            element("sourceGroupId", sourceGroupId),//
                            element("sourceArtifactId", sourceArtifactId),//
                            element("sourceVersion", version),//
                            element("groupId", groupId),//
                            element("exclusions", "portal-impl,portal-client,util-bridges,util-java,util-taglib"),//
                            element("startWith", startDependenciesWith),//
                            element("repositoryId", repositoryId),//
                            element("url", repositoryUrl),//
                            element("repositoryLayout", repositoryLayout)//
                    ),//
                    executionEnvironment(project, session, pluginManager)//
            );
        } catch (MojoExecutionException e) {
            log("could not deploy dependencies of [" + sourceGroupId + ":" + sourceArtifactId + "]: " + e.getCause().getMessage()
                    + ". Trying next one...");
        }
    }

    @Override
    protected void doUploadFile(File file, String groupId, String artifactId, String classifier, String packaging) {
        try {
            executeMojo(//
                    DEPLOY_PLUGIN,//
                    goal("deploy-file"),//
                    configuration(//
                            element("groupId", groupId),//
                            element("artifactId", artifactId),//
                            element("version", version),//
                            element("classifier", classifier),//
                            element("file", file.getAbsolutePath()),//
                            element("repositoryId", repositoryId),//
                            element("url", repositoryUrl),//
                            element("repositoryLayout", repositoryLayout),//
                            element("generatePom", "true"),//
                            element("packaging", packaging)
                    ),//
                    executionEnvironment(project, session, pluginManager)//
            );
        } catch (MojoExecutionException e) {
            log("could not deploy file [" + file + "]: " + e.getCause().getMessage() + ". Trying next one...");
        }
    }
}
