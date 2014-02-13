package org.mimacom.maven.plugins.liferay;


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

import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * @author nidi
 * @goal deployService
 */
public class DeployServiceMojo extends BaseLiferayMojo {
    /**
     * The ID of the repository to deploy to.
     *
     * @parameter expression="${project.distributionManagement.repository}"
     * @required
     */
    private DeploymentRepository repository;

    /**
     * The ID of the repository to deploy to.
     *
     * @parameter expression="${project.distributionManagement.snapshotRepository}"
     * @required
     */
    private DeploymentRepository snapshotRepository;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isServiceProject()) {
            File apiPom = new File(basedir, "src/api/pom.xml");
            deployApiProject(apiPom);
        }
    }

    private void deployApiProject(File apiPom) throws MojoExecutionException {
        boolean snapshot = project.getVersion().endsWith("-SNAPSHOT");
        DeploymentRepository repo = snapshot ? snapshotRepository : repository;

        try {
            executeMojo(//
                    DEPLOY_PLUGIN,//
                    goal("deploy-file"),//
                    configuration(//
                            element("pomFile", apiPom.getAbsolutePath()),//
                            element("file", apiArtifactJarFile().getAbsolutePath()),//
                            element("version", project.getVersion()),//
                            element("repositoryId", repo.getId()),//
                            element("url", repo.getUrl()),//
                            element("repositoryLayout", repo.getLayout()),//
                            element("uniqueVersion", "" + repo.isUniqueVersion())//
                    ),//
                    executionEnvironment(project, session, pluginManager)//
            );
        } catch (MojoExecutionException e) {
            throw new MojoExecutionException("Problem deploying the api.jar, maybe src/api/pom.xml is invalid", e.getCause());
        }
    }
}
