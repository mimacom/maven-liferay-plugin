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

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PropsUtil;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.dependency.fromConfiguration.ArtifactItem;
import org.codehaus.plexus.util.IOUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static org.edorasframework.tools.common.antfront.impl.Parameters.*;
import static org.edorasframework.tools.common.antfront.impl.Tasks.copy;
import static org.edorasframework.tools.common.antfront.impl.Tasks.delete;
import static org.edorasframework.tools.common.antfront.impl.Tasks.move;
import static org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;


/**
 * Liferay service builder mojo
 *
 * @author stni
 * @goal buildService2
 * @phase process-sources
 */
@SuppressWarnings({"JavaDoc"})
public class TwoArtifactsServiceBuilderMojo extends ProtoLiferayMojo {
    /**
     * @parameter default-value="true"
     * @required
     */
    private boolean autoNamespaceTables;

    /**
     * @parameter default-value="com.liferay.util.bean.PortletBeanLocatorUtil"
     * @required
     */
    private String beanLocatorUtil;

    /**
     * The build number to be used inside service.properties.
     *
     * @parameter expression="${buildNumber}" default-value="1"
     */
    private String buildNumber;

    /**
     * @parameter expression="${project.artifactId}"
     * @required
     */
    private String pluginName;

    /**
     * @parameter default-value="com.liferay.util.service.ServiceProps"
     * @required
     */
    private String propsUtil;

    /**
     * @parameter default-value="${basedir}/src/main/resources"
     * @required
     */
    private File resourcesDir;


    private final static FileFilter ALL_FILES = new FileFilter() {
        public boolean accept(File file) {
            return true;
        }
    };

    public void execute() throws MojoExecutionException {
        try {
            doExecute();
        } catch (ClassNotFoundException e) {
            handleClassNotFound(e);
        } catch (NoClassDefFoundError e) {
            handleClassNotFound(e);
        } catch (NoSuchMethodError e) {
            handleClassNotFound(e);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void handleClassNotFound(Throwable e) throws MojoExecutionException {
        throw new MojoExecutionException("Class not found or wrong version\nThis goal needs a dependency to liferay-adapter with version 1.1.0 or newer.\n" +
                "<build><plugins><plugin>\n" +
                "    <groupId>org.edorasframework.tools.maven</groupId>\n" +
                "    <artifactId>org.edorasframework.tools.maven.liferay</artifactId>\n" +
                "    <version>...</version>\n" +
                "    <extensions>true</extensions>\n" +
                "    <dependencies>\n" +
                "        <dependency>\n" +
                "            <groupId>org.mimacom.commons.liferay</groupId>\n" +
                "            <artifactId>liferay-adapter-${liferay-version}</artifactId>\n" +
                "            <version>1.1.0</version>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</plugin></plugins></build>", e);
    }

    protected void doExecute() throws Exception {
        if (!apiSourceDir().exists()) {
            apiSourceDir().mkdirs();
        }
        addSourceDir(apiSourceDir());

        String serviceFileName = "META-INF/service.xml";
        File serviceFile = new File(resourcesDir, serviceFileName);

        setArtifactType(new File(buildDir, finalName + ".war"), "war");
//        mavenHelper.attachArtifact(project, "jar", new File(buildDir, finalName + "-api.jar"));

        if (!serviceFile.exists()) {
            getLog().warn(serviceFile.getAbsolutePath() + " does not exist");
            return;
        }

        getLog().info("Building from " + serviceFile.getAbsolutePath());

        PropsUtil.set("spring.configs", "META-INF/service-builder-spring.xml");
        PropsUtil.set(PropsKeys.RESOURCE_ACTIONS_READ_PORTLET_RESOURCES, "false");

        InitUtil.initWithSpring();

        File ignoreDir = new File(buildDir, "liferay-generated");
        File apiDir = new File(ignoreDir, "api");
        File implDir = new File(ignoreDir, "impl");

        File metaInfIgnore = new File(implDir, "META-INF");
        File sqlDir = new File(implDir, "webapp/WEB-INF/sql");
        if (!sqlDir.exists()) {
            getLog().info("creating sql dir");
            sqlDir.mkdirs();
        }

        log("Copying src/main/java to liferay-generated/impl");
        copy(fileset(dir(sourceDir)), todir(implDir), overwrite(true));

        log("Copying src/api/java to liferay-generated/api");
        copy(fileset(dir(apiSourceDir())), todir(apiDir), overwrite(true));

        tools.initLog(getLog());
        tools.initLiferay();
        tools.buildService(serviceFile.getAbsolutePath(),//
                new File(metaInfIgnore, "portlet-hbm.xml").getAbsolutePath(),//
                new File(metaInfIgnore, "portlet-orm.xml").getAbsolutePath(),
                new File(metaInfIgnore, "portlet-model-hints.xml").getAbsolutePath(),//
                new File(metaInfIgnore, "portlet-spring.xml").getAbsolutePath(),//
                new File(metaInfIgnore, "base-spring.xml").getAbsolutePath(),//
                null, //
                new File(metaInfIgnore, "dynamic-data-source-spring.xml").getAbsolutePath(),//
                new File(metaInfIgnore, "hibernate-spring.xml").getAbsolutePath(),//
                new File(metaInfIgnore, "infrastructure-spring.xml").getAbsolutePath(),//
                new File(metaInfIgnore, "shard-data-source-spring.xml").getAbsolutePath(), //
                apiDir.getAbsolutePath(),//
                implDir.getAbsolutePath(),//
                new File(implDir, "webapp/html/js/liferay/service.js").getAbsolutePath(),//
                null,//
                sqlDir.getAbsolutePath(),//
                "tables.sql", "indexes.sql", "indexes.properties", "sequences.sql",//
                autoNamespaceTables, beanLocatorUtil, propsUtil, pluginName, null);

        log("Moving not customized apis from liferay-generated/api to generated-sources/api");
        File generatedApi = new File(buildDir, "generated-sources/api");
        makeComplementary(JAVA_FILES, apiDir, apiSourceDir(), generatedApi);
        addSourceDir(generatedApi);

        log("Moving not customized impls from liferay-generated/impl to generated-sources/impl");
        File generatedImpl = new File(buildDir, "generated-sources/impl");
        makeComplementary(JAVA_FILES, implDir, sourceDir, generatedImpl);
        addSourceDir(generatedImpl);

        log("Moving not customized modifyable impls from generated-sources/impl to src/main/java");
        move(fileset(dir(generatedImpl),
                includes("**/service/impl/*.java,**/model/impl/*.java"),
                excludes("**/*BaseImpl.java,**/*ModelImpl.java")),
                todir(sourceDir), overwrite(false));

        log("copying service.xml to WEB-INF");
        copy(file(resourcesDir, "META-INF/service.xml"), todir(buildDir, finalName + "/WEB-INF"));

        final String impl = "target/liferay-generated/impl/";
        log("setting build.number to " + buildNumber + " and copying service.properties from " + impl + " to " + outputDir);
        outputDir.mkdirs();
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(implDir, "service.properties")), "ISO-8859-1"));
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputDir, "service.properties")), "ISO-8859-1"));
            while (in.ready()) {
                String line = in.readLine();
                line = line.replaceFirst("(build\\.number\\s*=\\s*)\\d+", "$1" + buildNumber);
                out.write(line);
                out.newLine();
            }
        } finally {
            IOUtil.close(in);
            IOUtil.close(out);
        }

        log("copying from " + impl + "META-INF to " + outputDir + "/META-INF");
        copyNonExistingFiles(ALL_FILES, new File(implDir, "META-INF"), new File(outputDir, "META-INF"), new File(resourcesDir, "META-INF"));

        log("copying from " + impl + "webapp to " + buildDir + "/" + finalName);
        copyNonExistingFiles(ALL_FILES, new File(implDir, "webapp"), new File(buildDir, finalName), new File(resourcesDir, "webapp"));
    }

    /**
     * Make the contents of 'result' such that the files from 'result' plus the files from 'part' are the same as the files in 'all'
     * and there are no duplicates.
     *
     * @param all
     * @param part
     * @param result
     */
    private void makeComplementary(FileFilter filter, File all, File part, File result) {
        copyNonExistingFiles(filter, all, result, part);
        deleteExistingFiles(filter, result, part);
    }

    /**
     * Copy all files from 'from' to 'to' which do not exist in 'overwrites'
     *
     * @param filter
     * @param from
     * @param to
     * @param overwrites
     */
    private void copyNonExistingFiles(FileFilter filter, File from, File to, File overwrites) {
        for (File fromFile : from.listFiles(filter)) {
            String name = fromFile.getName();
            if (fromFile.isDirectory()) {
                copyNonExistingFiles(filter, fromFile, new File(to, name), new File(overwrites, name));
            } else if (!new File(overwrites, name).exists()) {
                //getLog().info("Copying " + fromFile + " to " + to);
                copy(file(fromFile), todir(to));
            }
        }
    }

    /**
     * Delete all files in 'dir' which exist also in 'overwrites'
     *
     * @param filter
     * @param dir
     * @param overwrites
     */
    private void deleteExistingFiles(FileFilter filter, File dir, File overwrites) {
        for (File overwrite : overwrites.listFiles(filter)) {
            String name = overwrite.getName();
            if (overwrite.isDirectory()) {
                deleteExistingFiles(filter, new File(dir, name), new File(overwrites, name));
            } else if (new File(dir, name).exists()) {
                //getLog().info("Deleting " + dir + "/" + name);
                delete(file(dir, name));
            }
        }
    }

    private String asString(ArtifactItem item) {
        return item.getGroupId() + ":" + item.getArtifactId() + ":jar:" + item.getVersion();
    }

    private Element asElement(ArtifactItem item) {
        return element("artifactItem", //
                element("groupId", item.getGroupId()), //
                element("artifactId", item.getArtifactId()),//
                element("version", item.getVersion()));
    }

    private Dependency asDependency(ArtifactItem item) {
        Dependency dep = new Dependency();
        dep.setGroupId(item.getGroupId());
        dep.setArtifactId(item.getArtifactId());
        dep.setVersion(item.getVersion());
        return dep;
    }


    private String apiArtifactId() {
        int pos = project.getArtifactId().lastIndexOf('-');
        if (pos < 0) {
            pos = 0;
        }
        return project.getArtifactId().substring(0, pos) + "-api";
    }

    /**
     * add api dir as new source dir
     */
    private void addSourceDir(File path) {
        if (!path.equals(sourceDir)) {
            getLog().info("Adding source dir " + path);
            project.addCompileSourceRoot(path.getAbsolutePath());
        }
    }
}