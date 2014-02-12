package org.mimacom.maven.plugins.liferay.prepare;


import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Install liferay artifacts to the local maven repository.
 * 
 * @author stni
 * @goal installLiferay
 * @requiresProject false
 */
public class InstallLiferayMojo extends AbstractUploadLiferayMojo {
    @Override
    protected void doUploadDependencies(String sourceGroupId, String sourceArtifactId, String groupId) {
        try {
            executeMojo(//
                WAR_MAVENIZER_PLUGIN,//
                goal("install"),//
                configuration(//
                    element("sourceGroupId", sourceGroupId),//
                    element("sourceArtifactId", sourceArtifactId),//
                    element("sourceVersion", version),//
                    element("groupId", groupId),//
                    element("exclusions", "portal-impl,portal-client,util-bridges,util-java,util-taglib"),//
                    element("startWith", startDependenciesWith)//
                ),//
                executionEnvironment(project, session, pluginManager)//
            );
        } catch (MojoExecutionException e) {
            log("could not install dependencies of [" + sourceGroupId + ":" + sourceArtifactId + "]: " + e.getCause().getMessage()
                    + ". Trying next one...");
        }
    }

    @Override
    protected void doUploadFile(File file, String groupId, String artifactId, String classifier, String packaging) {
        try {
            executeMojo(//
                INSTALL_PLUGIN,//
                goal("install-file"),//
                configuration(//
                    element("groupId", groupId),//
                    element("artifactId", artifactId),//
                    element("version", version),//
                    element("classifier", classifier),//
                    element("file", file.getAbsolutePath()),//
                    element("generatePom", "true"),//
                    element("packaging", packaging)//
                ),//
                executionEnvironment(project, session, pluginManager)//
            );
        } catch (MojoExecutionException e) {
            log("could not install file [" + file + "]: " + e.getCause().getMessage() + ". Trying next one...", e.getCause());
        }
    }
}
