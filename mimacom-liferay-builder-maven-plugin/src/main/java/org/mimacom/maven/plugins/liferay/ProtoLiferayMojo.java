package org.mimacom.maven.plugins.liferay;


import org.apache.maven.project.MavenProjectHelper;
import org.mimacom.commons.liferay.adapter.LiferayTools;

import java.io.File;
import java.io.FileFilter;


@SuppressWarnings({"JavaDoc"})
public abstract class ProtoLiferayMojo extends RealBaseLiferayMojo {
    protected final static FileFilter JAVA_FILES = new FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".java");
        }
    };

    /**
     * @component
     * @required
     */
    protected MavenProjectHelper mavenHelper;

    /**
     * @component
     */
    protected LiferayTools tools;

    /**
     * 
     * 
     * @parameter expression="${project.build.finalName}"
     * @required
     * @readonly
     */
    protected String finalName;

    /**
     * 
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected File buildDir;

    /**
     * 
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    protected File outputDir;

    /**
     * 
     * 
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    protected File sourceDir;

    /**
     * Normally 'target/'
     * 
     * @return
     */
    protected String simpleBuildDir() {
        return buildDir.getName() + "/";
    }

    /**
     * Normally 'target/finalName.war'
     *
     * @return
     */
    protected File artifactWarFile() {
        return new File(buildDir, finalName + ".war");
    }

    /**
     * Normally 'target/finalName.jar'
     *
     * @return
     */
    protected File artifactJarFile() {
        return new File(buildDir, finalName + ".jar");
    }

    /**
     * Normally 'target/finalName-api.jar'
     *
     * @return
     */
    protected File apiArtifactJarFile() {
        return new File(buildDir, finalName + "-api.jar");
    }

    protected boolean isServiceApiProject() {
        return project.getPackaging().equals("liferayServiceApi");
    }
    protected boolean isServiceProject() {
        return project.getPackaging().equals("liferayService");
    }

    protected File apiSourceDir() {
        return new File(basedir, "src/api/java");
    }

    protected boolean updateWebXml(Boolean updateWebXml) {
        if (updateWebXml != null) {
            return updateWebXml;
        }
        return !serverType().isAutoDeploy();
    }

    /**
     * Normally 'target/finalName'
     *
     * @return
     */
    protected File finalNameDir() {
        return new File(buildDir, finalName);
    }

}
