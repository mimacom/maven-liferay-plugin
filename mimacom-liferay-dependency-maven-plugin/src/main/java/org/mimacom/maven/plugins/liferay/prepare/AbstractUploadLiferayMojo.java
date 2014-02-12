package org.mimacom.maven.plugins.liferay.prepare;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.edorasframework.tools.common.sxp.write.Tag;
import org.mimacom.maven.plugins.liferay.ServerType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.edorasframework.tools.common.antfront.impl.Parameters.*;
import static org.edorasframework.tools.common.antfront.impl.Tasks.delete;
import static org.edorasframework.tools.common.antfront.impl.Tasks.expand;
import static org.edorasframework.tools.common.antfront.impl.Tasks.jar;
import static org.edorasframework.tools.common.antfront.impl.Tasks.war;
import static org.edorasframework.tools.common.sxp.write.Xml.attr;
import static org.edorasframework.tools.common.sxp.write.Xml.tag;


abstract class AbstractUploadLiferayMojo extends AbstractPrepareMojo {
    /**
     * A comma separated list of artifacts that should be uploaded. Possible values are: web, client, impl, bridges, java, taglib, deps,
     * kernel, service, implSrc, bridgeSrc, javaSrc, taglibSrc, kernelSrc, serviceSrc.
     *
     * @parameter expression="${includes}"
     */
    private String includes;

    /**
     * A comma separated list of artifacts that should NOT be uploaded. Possible values are: web, client, impl, bridges, java, taglib, deps,
     * kernel, service, implSrc, bridgeSrc, javaSrc, taglibSrc, kernelSrc, serviceSrc.
     *
     * @parameter expression="${excludes}"
     */
    private String excludes;

    /**
     * @parameter expression="${startDependenciesWith}"
     */
    protected String startDependenciesWith;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File target = new File(basedir, "target");

        log("deleting " + new File(target, "temp"));
        delete(dir(target, "temp"), failOnError(true));

        File exploded = new File(target, "exploded-" + version);

        File downloadedFile = findInputFile(serverType().downloadFilename(version), fileVersion());
        if (downloadedFile != null) {
            // TODO move into serverType
            if (serverType() == ServerType.TOMCAT) {
                String root = serverType().serverDirectoryInZip(version) + "/webapps/ROOT/**/*";
                String lib = serverType().serverDirectoryInZip(version) + "/lib/ext/*.jar";
                log("Extracting " + downloadedFile + " " + root + " and " + lib);
                File unzip = new File(target, "unzip-" + version);
                unzip.mkdir();
                expand(
                        src(downloadedFile),
                        patternset(includes(root + "," + lib)),
                        dest(unzip),
                        overwrite(true)
                );

                File tomcatDir = findTomcatDir(unzip);
                if (tomcatDir == null) {
                    log("tomcat not found inside " + unzip);
                } else {
                    File rootDir = new File(tomcatDir, "webapps/ROOT");
                    log("Creating portal.war from " + rootDir);
                    downloadedFile = new File(target, "unzipped-portal-with-dependencies-" + version + ".war");
                    delete(
                            file(downloadedFile),
                            failOnError(false)
                    );
                    war(
                            basedir(rootDir),
                            destFile(downloadedFile)
                    );

                    uploadCommonLibs(new File(tomcatDir, "lib/ext"), serverType().commonLibsGroupId());
                }
            }
            final String liferayWithDeps = "portal-web-with-dependencies";
            uploadFile("web", downloadedFile, LIFERAY_GROUP_ID, liferayWithDeps, "", "war");
            expand(
                    src(downloadedFile),
                    patternset(includes("WEB-INF/lib/portal-client.jar,WEB-INF/lib/portal-impl.jar,WEB-INF/lib/util-bridges.jar,WEB-INF/lib/util-java.jar,WEB-INF/lib/util-taglib.jar")),
                    dest(exploded),
                    overwrite(true)
            );
            File explodedLib = new File(exploded, "WEB-INF/lib");
            uploadJarFile("client", new File(explodedLib, "portal-client.jar"), LIFERAY_GROUP_ID, "portal-client");
            uploadJarFile("impl", new File(explodedLib, "portal-impl.jar"), LIFERAY_GROUP_ID, "portal-impl");
            uploadJarFile("bridges", new File(explodedLib, "util-bridges.jar"), LIFERAY_GROUP_ID, "util-bridges");
            uploadJarFile("java", new File(explodedLib, "util-java.jar"), LIFERAY_GROUP_ID, "util-java");
            uploadJarFile("taglib", new File(explodedLib, "util-taglib.jar"), LIFERAY_GROUP_ID, "util-taglib");

            uploadDependencies("deps", LIFERAY_GROUP_ID, liferayWithDeps, LIFERAY_DEPENDENCY_GROUP_ID);
        }

        File dependenciesFile = findInputFile("liferay-portal-dependencies.zip", fileVersion());
        if (dependenciesFile != null) {
            expand(
                    src(dependenciesFile),
                    dest(exploded),
                    overwrite(true)
            );
            File explodedDeps = new File(exploded, "liferay-portal-dependencies-" + fileVersion());
            if (!explodedDeps.exists()) {
                explodedDeps = new File(exploded, "liferay-portal-dependencies-" + version);
            }
            if (!explodedDeps.exists()) {
                throw new MojoFailureException("Neither 'liferay-portal-dependencies-" + fileVersion() + "' nor 'liferay-portal-dependencies-" + version + "' found inside " + dependenciesFile + ". NOT uploading dependencies.");
            }
            uploadCommonLibs(explodedDeps, LIFERAY_COMMON_GROUP_ID);
            uploadJarFile("kernel", new File(explodedDeps, "portal-kernel.jar"), LIFERAY_GROUP_ID, "portal-kernel");
            uploadJarFile("service", new File(explodedDeps, "portal-service.jar"), LIFERAY_GROUP_ID, "portal-service");
        }

        // TODO portal-client?
        String srcName = "liferay-portal-src.zip";
        File srcFile = findInputFile(srcName, fileVersion());
        if (srcFile != null) {
            if (!upload("src")) {
                log("Sources skipped");
            } else {
                log("Unzipping " + srcFile.getName() + " into " + exploded);
                expand(
                        src(srcFile),
                        patternset(includes(srcFolder("portal-impl") + "," +
                                srcFolder("portal-kernel") + "," +
                                srcFolder("portal-service") + "," +
                                srcFolder("util-bridges") + "," +
                                srcFolder("util-java") + "," +
                                srcFolder("util-taglib"))),
                        dest(exploded),
                        overwrite(true)
                );
                File explodedSrc = new File(exploded, "src");
                explodedSrc.mkdir();
                File srcSrc1 = new File(exploded, "liferay-portal-src-" + fileVersion());
                File srcSrc2 = new File(exploded, "liferay-portal-src-" + version);

                File srcSrc = srcSrc1;
                if (!srcSrc.exists()) {
                    srcSrc = srcSrc2;
                }
                if (!srcSrc.exists()) {
                    log("Neither " + srcSrc1 + " nor " + srcSrc2 + " exist. ");
                    log("Try renaming liferay-portal-src-" + fileVersion() + ".zip such that its name matches the content of " + exploded);
                } else {
                    jarSrc(srcSrc, "portal-impl", explodedSrc);
                    jarSrc(srcSrc, "portal-kernel", explodedSrc);
                    jarSrc(srcSrc, "portal-service", explodedSrc);
                    jarSrc(srcSrc, "util-bridges", explodedSrc);
                    jarSrc(srcSrc, "util-java", explodedSrc);
                    jarSrc(srcSrc, "util-taglib", explodedSrc);

                    uploadSrcFile("implSrc", new File(explodedSrc, "portal-impl.jar"), LIFERAY_GROUP_ID, "portal-impl");
                    uploadSrcFile("kernelSrc", new File(explodedSrc, "portal-kernel.jar"), LIFERAY_GROUP_ID, "portal-kernel");
                    uploadSrcFile("serviceSrc", new File(explodedSrc, "portal-service.jar"), LIFERAY_GROUP_ID, "portal-service");
                    uploadSrcFile("bridgesSrc", new File(explodedSrc, "util-bridges.jar"), LIFERAY_GROUP_ID, "util-bridges");
                    uploadSrcFile("javaSrc", new File(explodedSrc, "util-java.jar"), LIFERAY_GROUP_ID, "util-java");
                    uploadSrcFile("taglibSrc", new File(explodedSrc, "util-taglib.jar"), LIFERAY_GROUP_ID, "util-taglib");
                }
            }
        }

    }

    private String srcFolder(String jar) {
        return "liferay-portal-src-*/" + jar + "/src/**";
    }

    private void uploadCommonLibs(File dir, String groupId) {
        List<Tag> tags = new ArrayList<Tag>();
        File[] files = dir.listFiles();
        if (files == null) {
            log("Could not find " + dir + ". NOT uploading common libs.");
            return;
        }
        for (File commonLib : files) {
            if (commonLib.isFile() && commonLib.getName().endsWith(".jar")) {
                String artifactId = commonLib.getName().substring(0, commonLib.getName().length() - 4);
                uploadJarFile("commonLibs", commonLib, groupId, artifactId);
                tags.add(dependency(groupId, artifactId, version));
            }
        }
        try {
            File allPom = createAllPom(dir, groupId, tags);
            uploadFile("commonLibs", allPom, groupId, "all", "", "pom");
        } catch (IOException e) {
            log("Could not write [" + groupId + "] all.pom");
        }
    }

    private Tag dependency(String groupId, String artifactId, String version) {
        return tag("dependency",
                tag("groupId", groupId),
                tag("artifactId", artifactId),
                tag("version", version)
        );
    }

    private File createAllPom(File dir, String groupId, List<Tag> deps) throws IOException {
        File all = new File(dir, groupId + "-all.pom");
        Writer out = new OutputStreamWriter(new FileOutputStream(all), "UTF-8");
        tag("project",
                attr("xmlns", "http://maven.apache.org/POM/4.0.0"),
                attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"),
                attr("xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd"),
                tag("modelVersion", "4.0.0"),
                tag("groupId", groupId),
                tag("artifactId", "all"),
                tag("version", version),
                tag("packaging", "pom"),
                tag("dependencies", deps)
        ).write(out);
        out.close();
        return all;
    }

    private File findTomcatDir(File basedir) {
        for (File f : basedir.listFiles()) {
            if (f.isDirectory() && f.getName().contains("tomcat-")) {
                return f;
            }
            if (f.isDirectory() && f.getName().startsWith("liferay-portal-")) {
                for (File f2 : f.listFiles()) {
                    if (f2.isDirectory() && f2.getName().startsWith("tomcat-")) {
                        return f2;
                    }
                }
            }
        }
        return null;
    }

    private File findInputFile(String name, String version) {
        if (version != null) {
            int pos = name.lastIndexOf('.');
            name = name.substring(0, pos) + "-" + version + name.substring(pos);
        }
        File f = new File(basedir, name);
        if (f.exists()) {
            return f;
        }
        f = new File(new File(basedir, "target"), name);
        if (f.exists()) {
            return f;
        }
        log("File [" + name + "] does not exist in base directory nor in target. Skipping.");
        return null;
    }

    private void jarSrc(File source, String name, File dest) {
        try {
            jar(
                    basedir(source, name + "/src"),
                    destFile(dest, name + ".jar")
            );
        } catch (Exception e) {
            log("could not upload [" + source + "]: " + e.getMessage() + ". Trying next one...");
        }
    }

    private void uploadDependencies(String id, String sourceGroupId, String sourceArtifactId, String groupId) {
        if (!upload(id)) {
            log("[" + sourceGroupId + ":" + sourceArtifactId + "] skipped");
            return;
        }
        doUploadDependencies(sourceGroupId, sourceArtifactId, groupId);
    }

    protected abstract void doUploadDependencies(String sourceGroupId, String sourceArtifactId, String groupId);


    private void uploadJarFile(String id, File file, String groupId, String artifactId) {
        uploadFile(id, file, groupId, artifactId, "", "jar");
    }

    private void uploadSrcFile(String id, File file, String groupId, String artifactId) {
        uploadFile(id, file, groupId, artifactId, "sources", "jar");
    }

    protected abstract void doUploadFile(File file, String groupId, String artifactId, String classifier, String packaging);

    private void uploadFile(String id, File file, String groupId, String artifactId, String classifier, String packaging) {
        if (!upload(id)) {
            log("File [" + file + "] skipped");
            return;
        }
        doUploadFile(file, groupId, artifactId, classifier, packaging);
    }

    private boolean upload(String id) {
        return ((includes == null || includes.contains(id)) && (excludes == null || !excludes.contains(id)));
    }
}
