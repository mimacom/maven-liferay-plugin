package org.mimacom.maven.plugins.liferay;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

import static org.edorasframework.tools.common.antfront.impl.Parameters.*;
import static org.edorasframework.tools.common.antfront.impl.Tasks.copy;
import static org.edorasframework.tools.common.antfront.impl.Tasks.delete;


/**
 * Deploy an existing .war-file to an application server by copying it to a given directory.
 *
 * @author stni
 * @goal installWar
 *
 */
public class InstallWarMojo extends InstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isCopy(copyDeploy) && createWar) {
            File dest;
            if (isPortalProject(project)) {
                dest = serverType().serverDeployDirectory(this);
                getLog().info("deleting " + dest + "/" + finalName);
                delete(//
                    dir(dest, finalName),//
                    failOnError(true)//
                );
            } else {
                dest = deployDirectory();
            }
            getLog().info("copying " + finalName + ".war to " + dest);
            copy(//
                file(artifactWarFile()),//
                todir(dest),//
                overwrite(true)//
            );
        }
    }

}
