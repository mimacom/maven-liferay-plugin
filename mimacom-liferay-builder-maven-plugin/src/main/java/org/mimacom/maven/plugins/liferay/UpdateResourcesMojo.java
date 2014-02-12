package org.mimacom.maven.plugins.liferay;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

import static org.edorasframework.tools.common.antfront.impl.Parameters.*;
import static org.edorasframework.tools.common.antfront.impl.Tasks.copy;




/**
 * Copy the web resources (src/main/webapp/**) of a project to an existing installation of the project. <br/>
 * The following subfolders are excluded: WEB-INF/** (WEB-INF/tag/** is included), META-INF/**
 * 
 * @author stni
 * @goal updateResources
 * 
 */
public class UpdateResourcesMojo extends BaseLiferayMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        File dir = serverType().serverExplodedDirectory(this);
        if (dir == null || !dir.exists()) {
            getLog().info("resources (" + WEBAPP_DIR + ") are NOT copied to " + dir + " since the target folder does not exist.");
        } else {
            getLog().info("copying resources (" + WEBAPP_DIR + ") to " + dir);
            copy(//
                fileset(//
                    dir(basedir, WEBAPP_DIR),//
                    includes("**"),//
                    excludes("WEB-INF/**,META-INF/**,_diffs/**")//
                ),//
                fileset(//
                    dir(basedir, WEBAPP_DIR),//
                    includes("WEB-INF/tag/**")//
                ),//
                todir(dir),//
                overwrite(true)//
            );

            File diffsDir = new File(basedir, WEBAPP_DIR + "/_diffs");
            if (diffsDir.exists()) {
                // copy files from src/main/webapp/_diffs
                log("copying _diffs from " + WEBAPP_DIR);
                copy(//
                    fileset(dir(diffsDir)),//
                    todir(dir),//
                    overwrite(true)//
                );
            }
        }
    }
}
