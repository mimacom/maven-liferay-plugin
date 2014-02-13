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

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import static org.edorasframework.tools.common.antfront.impl.Parameters.file;
import static org.edorasframework.tools.common.antfront.impl.Parameters.todir;
import static org.edorasframework.tools.common.antfront.impl.Parameters.upDirectoryScanner;
import static org.edorasframework.tools.common.antfront.impl.Tasks.move;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Liferay service builder mojo
 *
 * @author stni
 * @goal packageServiceApi
 * @phase package
 */
public class PackageServiceApiBuilderMojo extends ProtoLiferayMojo {

    public void execute() throws MojoExecutionException {
        try {
            doExecute();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected void doExecute() throws Exception {
        File to = new File(buildDir, finalName + "-api");
        log("Moving classes from src/api/java to " + to);
        moveExistingClasses(outputDir, to, apiSourceDir());
        log("Moving classes from target/generated-sources/api to " + to);
        moveExistingClasses(outputDir, to, new File(buildDir, "generated-sources/api"));

        executeMojo(//
                JAR_PLUGIN,//
                goal("jar"),//
                configuration(//
                        element("classesDirectory", to.getAbsolutePath()),//
                        element("finalName", to.getName())//
                ),//
                executionEnvironment(project, session, pluginManager)//
        );
    }

    private void moveExistingClasses(File from, File to, File sources) {
        for (File source : sources.listFiles(JAVA_FILES)) {
            String name = source.getName();
            if (source.isDirectory()) {
                moveExistingClasses(new File(from, name), new File(to, name), source);
            } else {
                String className = name.substring(0, name.length() - 4) + "class";

                if (new File(from, className).exists()) {
                    //getLog().info("Moving " + from + "/" + className + " to " + to);
                    move(file(from, className), todir(to));
                }

                /* Search for class files generated for named and anonymous inner classes, for e.g.:
                 * - ModelName$InnerClass.class
                 * - ModelName$1.class
                 * - ModelName$2.class
                 */
                String regExString = name.substring(0, name.length() - 5) + "\\$.+\\.class";
                File[] innerClasses = from.listFiles((FilenameFilter)new RegexFileFilter(regExString));

                for (int i = 0; i < innerClasses.length; i++) {
                    File innerClass = innerClasses[i];
                    move(file(from, innerClass.getName()), todir(to));
                }
            }
        }
    }

}