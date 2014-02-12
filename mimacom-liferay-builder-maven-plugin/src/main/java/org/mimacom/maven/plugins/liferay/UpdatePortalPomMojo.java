package org.mimacom.maven.plugins.liferay;


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
