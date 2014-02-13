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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

import static org.edorasframework.tools.common.antfront.impl.Parameters.*;
import static org.edorasframework.tools.common.antfront.impl.Tasks.copy;


/**
 * Update a deployed portal by copying the configuration files to a given directory.
 * 
 * @author stni
 * @goal updatePortal
 */
public class UpdatePortalMojo extends InstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        // do not update if target folder does not exist
        File serverDeployDir = serverType().serverDeployDirectory(this);
        if (isCopy(copyDeploy) && new File(serverDeployDir, finalName).exists()) {
            File webapp = new File(basedir, WEBAPP_DIR);
            if (webapp.exists()) {
                log("copying " + WEBAPP_DIR + " to " + serverDeployDir + "/" + finalName);
                copy(//
                    fileset(dir(webapp)),//
                    overwrite(true),//
                    todir(serverDeployDir, finalName)//
                );
            }

            File classes = new File(buildDir, finalName + "/WEB-INF/classes");
            if (classes.exists()) {
                log("copying " + simpleBuildDir() + finalName + "/WEB-INF/classes to " + serverDeployDir + "/" + finalName
                        + "/WEB-INF/classes");
                copy(//
                    fileset(//
                        dir(classes),//
                        excludes(ORIGINAL_CONFIGS)//
                    ),//
                    todir(serverDeployDir, finalName + "/WEB-INF/classes")//
                );
            }
        }
    }
}
