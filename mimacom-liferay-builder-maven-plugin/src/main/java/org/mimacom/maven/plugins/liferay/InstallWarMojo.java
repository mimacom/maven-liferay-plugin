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
