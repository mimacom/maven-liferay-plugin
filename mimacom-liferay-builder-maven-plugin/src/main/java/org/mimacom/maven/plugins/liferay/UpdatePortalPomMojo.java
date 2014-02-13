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

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.util.Arrays;


/**
 * @author stni
 * @goal updatePortalPom
 */
public class UpdatePortalPomMojo extends BaseLiferayMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            MavenProject liferayWarProject = resolveArtifact(liferayWar.getGroupId(), liferayWar.getArtifactId(),
                    liferayWar.getVersion());
            if (isPortalProject(liferayWarProject)) {
                log("trying to extract " + liferayWar + "-config/-commonLib to " + configDir() + "/" + commonLibDir());
                try {
                    unpackLiferayWarArtifact(configDir(), "config", "zip");
                    unpackLiferayWarArtifact(commonLibDir(), "commonLib", "zip");
                } catch (MojoExecutionException e) {
                    log("liferay war artifact '" + liferayWar + "' seems not to contain configuration zip.");
                }
            }
        } catch (Exception e) {
            log("Could not handle config and commonLibs of liferayWar! " + e.getMessage());
        }

        // setting output directory
        log("setting output directory to " + outputDirectory());
        project.getBuild().setOutputDirectory(outputDirectory());

        // adjust main resource
        log("setting resource " + RESOURCES_DIR + " to target " + outputDirectory());
        Resource mainResource = getOrCreateResource(RESOURCES_DIR);
        mainResource.setFiltering(true);
        mainResource.setTargetPath(".");

        // adjust config resource
        String configTargetPath = "../../../../" + configDir();
        log("setting resource " + CONFIG_DIR + " to target " + outputDirectory() + "/" + configTargetPath);
        Resource configResource = getOrCreateResource(CONFIG_DIR);
        configResource.setFiltering(true);
        configResource.setIncludes(Arrays.asList("*", "META-INF/**/*"));
        configResource.setExcludes(Arrays.asList(ORIGINAL_CONFIGS.split(",")));
        configResource.setTargetPath(configTargetPath);

        // adjust webapp resource
        log("setting resource " + WEBAPP_DIR + " to target " + outputDirectory() + "/../..");
        Resource webappResource = getOrCreateResource(WEBAPP_DIR);
        webappResource.setTargetPath("../..");
    }

    /**
     * @return Normally 'target/finalName/WEB-INF/classes'
     */
    private String outputDirectory() {
        return finalNameDir() + "/WEB-INF/classes";
    }

}
