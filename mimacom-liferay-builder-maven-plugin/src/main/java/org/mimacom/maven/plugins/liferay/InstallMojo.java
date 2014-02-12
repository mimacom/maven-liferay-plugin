package org.mimacom.maven.plugins.liferay;


import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;


public abstract class InstallMojo extends BaseLiferayMojo {
    /**
     * The directory the resulting artifact should copied to.
     * 
     * @parameter expression="${deployDirectory}"
     */
    File deployDirectory;

    protected File deployDirectory() throws MojoExecutionException {
        return deployDirectory != null ? check("deployDirectory", deployDirectory) : new File(liferayDirectory(), "deploy");
    }

    /**
     * If the resulting artifact should be copied.
     * 
     * @parameter expression="${copyDeploy}"
     */
    protected Boolean copyDeploy;

}
