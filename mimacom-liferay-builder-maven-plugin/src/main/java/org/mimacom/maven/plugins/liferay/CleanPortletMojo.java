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
