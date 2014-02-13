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

import com.yahoo.platform.yui.compressor.YUICompressor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.List;

import static org.edorasframework.tools.common.antfront.impl.Parameters.*;
import static org.edorasframework.tools.common.antfront.impl.Tasks.copy;
import static org.edorasframework.tools.common.antfront.impl.Tasks.delete;
import static org.edorasframework.tools.common.antfront.impl.Tasks.move;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Create a .war-file of a (portlet/theme/layout) project.
 * 
 * @author stni
 * @goal createWar
 */
public class CreateWarMojo extends BaseLiferayMojo {

    /**
     * @parameter expression="${type}" default-value="portlet"
     */
    private String type;

    /**
     * A comma separated list of jar files that should be excluded from the resulting WEB-INF/lib.
     * 
     * @parameter
     */
    private String excludeLibs;

    /**
     * If deployment descriptors should be filtered.
     * 
     * @parameter default-value="true"
     */
    private boolean filteringDeploymentDescriptors;

    /**
     * Define whether to compress the css files or not.
     * 
     * @parameter default-value="false"
     */
    private boolean compressCss;

    /**
     * A comma separated list of files extensions which are not to be filtered
     * 
     * @parameter
     */
    private String nonFilteredFileExtensions;

    /**
     * If the plugin should update automatically the web.xml file. Needed only if liferay auto deployment is not used.
     * 
     * @parameter expression="${updateWebXml}"
     */
    protected Boolean updateWebXml;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isServiceApiProject()){
            return;
        }

        setArtifactType(artifactWarFile(), "war");
        
        List<String> filters = project.getBuild().getFilters();
        Element[] filterElements = new Element[filters.size()];
        int i = 0;
        for (String filter : filters) {
            filterElements[i++] = element(name("filter"), filter);
        }

        Element[] warConfig = new Element[3];
        warConfig[0] = element(name("filteringDeploymentDescriptors"), Boolean.toString(filteringDeploymentDescriptors));
        warConfig[1] = element(name("nonFilteredFileExtensions"), nonFilteredFileExtensions);
        warConfig[2] = element(name("filters"), filterElements);

        log("exploding into target, using filters: " + filters);
        executeMojo(//
            WAR_PLUGIN,//
            goal("exploded"),//
            configuration(warConfig),//
            executionEnvironment(project, session, pluginManager)//
        );
        try {
            log("initializing liferay");
            tools.initLog(getLog());
            tools.initLiferay();

            if (excludeLibs != null) {
                log("deleting WEB-INF/lib/" + excludeLibs);
                delete(//
                    dir(buildDir, finalName + "/WEB-INF/lib"),//
                    includes(excludeLibs)//
                );
            }

            log("deleting existing war file");
            delete(file(artifactWarFile()));

            if ("layout".equals(type) || finalName.contains("layout")) {
                handleLayout();
            } else if ("theme".equals(type) || finalName.contains("theme")) {
                handleTheme();
            } else if ("hook".equals(type) || finalName.contains("hook")) {
                handleHook();
            } else {
                handlePortlet();
            }

            createWar(finalNameDir(), null);

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * @throws Exception
     */
    protected void handleLayout() throws Exception {
        if (updateWebXml(updateWebXml)) {
            log("updating as layout");
            tools.deployLayout(serverType, finalNameDir());
        }
    }

    /**
     * @throws Exception
     */
    protected void handlePortlet() throws Exception {
        if (updateWebXml(updateWebXml)) {
            log("updating as portlet");
            tools.deployPortlet(project.getVersion(), serverType, finalNameDir());
        }
    }

    /**
     * @throws Exception
     */
    protected void handleHook() throws Exception {
        if (updateWebXml(updateWebXml)) {
            log("updating as hook");
            tools.deployHook(serverType, finalNameDir());
        }
    }

    /**
     * @throws MojoExecutionException
     * @throws Exception
     */
    protected void handleTheme() throws Exception {
        executeMojo(//
            DEPENDENCY_PLUGIN,//
            goal("unpack"),//
            configuration(//
                element("outputDirectory", finalNameDir().getAbsolutePath()),//
                element("overWriteReleases", "true"),//
                element("includes", "html/themes/classic/**/*"),//
                element("artifactItems", liferayArtifactItemElement(null, "war")//
                )//
            ),//
            executionEnvironment(project, session, pluginManager)//
        );

        log("Copying classic theme files to " + finalName);
        move(//
            fileset(dir(finalNameDir(), "html/themes/classic")),//
            todir(finalNameDir())//
        );


        log("Copying files from " + WEBAPP_DIR + " to " + finalName);
        copy(//
            fileset(dir(basedir, WEBAPP_DIR)),//
            todir(finalNameDir()),//
            overwrite(true)//
        );

        File diffsDir = new File(basedir, WEBAPP_DIR + "/_diffs");
        if (diffsDir.exists()) {
            log("copying _diffs from " + WEBAPP_DIR + " to " + finalName);
            copy(//
                fileset(dir(diffsDir)),//
                todir(finalNameDir()),//
                overwrite(true)//
            );
        }

        compressCss(finalNameDir());

        tools.mergeCss(finalNameDir());

        if (updateWebXml(updateWebXml)) {
            log("updating as theme");
            tools.deployTheme(serverType, finalNameDir());
        }

        delete(dir(finalNameDir(), "html"), failOnError(false));
        delete(dir(finalNameDir(), "_diffs"), failOnError(false));
    }

    public void compressCss(File basedir) {
        if (compressCss) {
            getLog().info("compressing css files");
            compressCssInternal(new File(basedir, "css"));
        }
    }

    private void compressCssInternal(File dir) {
        if (dir.exists()) {
            // compress css
            File[] cssFiles = dir.listFiles();

            for (File file : cssFiles) {
                if (file.isDirectory()) {
                    compressCssInternal(file);
                } else if (file.getName().endsWith("css")) {
                    YUICompressor.main(new String[] { "--type", "css", "-o", file.getAbsolutePath(), file.getAbsolutePath() });
                }
            }
        }
    }
   }
