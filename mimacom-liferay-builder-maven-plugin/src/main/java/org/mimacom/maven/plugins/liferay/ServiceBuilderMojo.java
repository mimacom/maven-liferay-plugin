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
import java.util.List;

import static org.edorasframework.tools.common.antfront.impl.Parameters.file;
import static org.edorasframework.tools.common.antfront.impl.Parameters.todir;
import static org.edorasframework.tools.common.antfront.impl.Tasks.copy;
import static org.edorasframework.tools.common.antfront.impl.Tasks.delete;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Liferay service builder mojo
 * 
 * @goal buildService
 * @phase process-sources
 * @author stni
 */
@SuppressWarnings({"JavaDoc"})
public class ServiceBuilderMojo extends ProtoLiferayMojo {
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
     * The artifact containing the API. This is only used with 'liferayServiceImpl' packaging and no file
     * 'src/main/resources/META-INF/service.xml' exists. Default is this artifact id where the last part is replaced by 'api'. E.g. if this
     * artifact id is 'my-service-impl' then apiArtifact is 'my-service-api'.
     * 
     * @parameter expression="${apiArtifact}"
     */
    private ArtifactItem apiArtifact;

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

    /**
     * If the plugin should update automatically the web.xml file. Needed only if liferay auto deployment is not used.
     *
     * @parameter expression="${updateWebXml}"
     */
    protected Boolean updateWebXml;

    private final static FileFilter JAVA_FILES = new FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".java");
        }
    };

    private final static FileFilter ALL_FILES = new FileFilter() {
        public boolean accept(File file) {
            return true;
        }
    };

    public void execute() throws MojoExecutionException {
        try {
            doExecute();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected void doExecute() throws Exception {
        File ignoreDir = new File(buildDir, "ignore");
        String serviceFileName = "META-INF/service.xml";
        File serviceFile = new File(resourcesDir, serviceFileName);

        boolean checkApi = false;
        boolean checkImpl = false;

        if (isServiceApiProject()) {
            checkApi = true;
            setArtifactType(new File(buildDir, finalName + ".jar"), "jar");
        } else {
            checkImpl = true;
            setArtifactType(artifactWarFile(), "war");
            if (serviceFile.exists()) {
                log("found " + serviceFile.getAbsolutePath() + ", not looking for api dependency");
                checkApi = true;
            } else {
                serviceFile = findServiceFile(serviceFileName);
            }
        }

        if (!serviceFile.exists()) {
            getLog().warn(serviceFile.getAbsolutePath() + " does not exist");
            return;
        }

        getLog().info("Building from " + serviceFile.getAbsolutePath());


        PropsUtil.set("spring.configs", "META-INF/service-builder-spring.xml");
        PropsUtil.set(PropsKeys.RESOURCE_ACTIONS_READ_PORTLET_RESOURCES, "false");

        InitUtil.initWithSpring();

        File apiDir = new File(ignoreDir, "api");
        File implDir = new File(ignoreDir, "impl");

        File metaInfIgnore = new File(implDir, "META-INF");
        File sqlDir = new File(implDir, "webapp/WEB-INF/sql");
        if (!sqlDir.exists()) {
            getLog().info("creating sql dir");
            sqlDir.mkdirs();
        }

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

        if (checkApi) {
            checkSources(apiDir, new File(buildDir, "generated-sources/api"));
        }
        if (checkImpl) {
            checkSources(implDir, new File(buildDir, "generated-sources/impl"));
        }

        if (!isServiceApiProject()) {
            String impl = "target/ignore/impl/";

            log("copying from service.xml to WEB-INF");
            copy(file(buildDir, "META-INF/service.xml"), todir(buildDir, finalName + "/WEB-INF"));

            log("setting build.number to " + buildNumber + " and copying service.properties from " + impl + " to " + outputDir);
            BufferedReader in = null;
            BufferedWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(implDir, "service.properties")), "ISO-8859-1"));
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputDir, "service.properties")),
                        "ISO-8859-1"));
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
            copyNonExistingFiles(ALL_FILES, new File(implDir, "META-INF"), new File(outputDir, "META-INF"), new File(resourcesDir,
                    "META-INF"));

            log("copying from " + impl + "webapp to " + buildDir + "/" + finalName);
            copyNonExistingFiles(ALL_FILES, new File(implDir, "webapp"), new File(buildDir, finalName), new File(resourcesDir, "webapp"));
        }
    }

    private File findServiceFile(String serviceFileName) throws MojoExecutionException {
        log("searching for api dependency " + asString(apiArtifactItem()));
        try {
            executeMojo(//
                DEPENDENCY_PLUGIN,//
                goal("unpack"),//
                configuration(//
                    element("artifactItems", asElement(apiArtifactItem())),//
                    element("includes", serviceFileName),//
                    element("outputDirectory", simpleBuildDir())//
                ),//
                executionEnvironment(project, session, pluginManager)//
            );

            Dependency apiDep = asDependency(apiArtifactItem());
            apiDep.setScope("provided");
            log("adding dependency to " + apiDep);
            @SuppressWarnings("unchecked")
            List<Dependency> deps = project.getDependencies();
            deps.add(apiDep);
        } catch (MojoExecutionException e) {
            throw new MojoExecutionException("artifact for API not found: " + asString(apiArtifactItem()), e.getCause());
        }
        return new File(buildDir, serviceFileName);
    }

    private void checkSources(File generatedDir, File destDir) {
        copyNonExistingFiles(JAVA_FILES, generatedDir, destDir, sourceDir);
        deleteExistingFiles(JAVA_FILES, destDir, sourceDir);
        addSourceDir(destDir);
    }

    private void copyNonExistingFiles(FileFilter filter, File from, File to, File overwrites) {
        for (File fromFile : from.listFiles(filter)) {
            String name = fromFile.getName();
            if (fromFile.isDirectory()) {
                copyNonExistingFiles(filter, fromFile, new File(to, name), new File(overwrites, name));
            } else if (!new File(overwrites, name).exists()) {
                copy(file(fromFile), todir(to));
            }
        }
    }

    private void deleteExistingFiles(FileFilter filter, File dir, File overwrites) {
        for (File overwrite : overwrites.listFiles(filter)) {
            String name = overwrite.getName();
            if (overwrite.isDirectory()) {
                deleteExistingFiles(filter, new File(dir, name), new File(overwrites, name));
            } else if (new File(dir, name).exists()) {
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

    private ArtifactItem apiArtifactItem() {
        ArtifactItem item = new ArtifactItem();
        item.setGroupId(apiArtifact.getGroupId() != null ? apiArtifact.getGroupId() : project.getGroupId());
        item.setArtifactId(apiArtifact.getArtifactId() != null ? apiArtifact.getArtifactId() : apiArtifactId());
        item.setVersion(apiArtifact.getVersion() != null ? apiArtifact.getVersion() : project.getVersion());
        return item;
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