package org.mimacom.maven.plugins.liferay;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

import static org.edorasframework.tools.common.antfront.impl.Parameters.dir;
import static org.edorasframework.tools.common.antfront.impl.Parameters.failOnError;
import static org.edorasframework.tools.common.antfront.impl.Tasks.delete;


/**
 * Delete the deployed portlet out of the server.
 * 
 * @author stni
 * @goal cleanPortlet
 * 
 */
public class CleanPortletMojo extends InstallMojo {
    /**
     * If the directory with the name of the war file should be deleted on a 'mvn clean'.
     * 
     * @parameter expression="${cleanDeployedPortlet}"
     */
    private Boolean cleanDeployedPortlet;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File deployDir = serverType().serverDeployDirectory(this);
        if (deployDir != null && isCopy(cleanDeployedPortlet)) {
            log("Deleting " + deployDir + "/" + finalName);
            delete(//
                dir(deployDir, finalName),//
                failOnError(false)//
            );
        }
    }

}
