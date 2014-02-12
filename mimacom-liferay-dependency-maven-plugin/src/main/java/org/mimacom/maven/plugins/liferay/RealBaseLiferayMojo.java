package org.mimacom.maven.plugins.liferay;


import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.DelegatingPluginManager;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;

//IMPORTANT: This class and its superclasses must NOT depend on LiferayTools! Not even in the import declaration.
@SuppressWarnings({"JavaDoc"})
public abstract class RealBaseLiferayMojo extends AbstractMojo {
    protected static final String LIFERAY_GROUP_ID = "com.liferay.portal";

    protected static final String LIFERAY_DEPENDENCY_GROUP_ID = LIFERAY_GROUP_ID + ".dependency";

    protected static final String LIFERAY_COMMON_GROUP_ID = LIFERAY_GROUP_ID + ".common";

    protected static final Plugin DEPLOY_PLUGIN = standardPlugin("deploy", "2.5");

    protected static final Plugin DEPENDENCY_PLUGIN = standardPlugin("dependency", "2.2");

    protected static final Plugin INSTALL_PLUGIN = standardPlugin("install", "2.3.1");

    protected static final Plugin JAR_PLUGIN = standardPlugin("jar", "2.3.2");

    protected static final Plugin WAR_PLUGIN = standardPlugin("war", "2.1.1");

    protected static final Plugin WAR_MAVENIZER_PLUGIN = plugin("org.edorasframework.tools.maven",
            "org.edorasframework.tools.maven.warmavenizer", "1.0.11");

    private static Plugin standardPlugin(String name, String version) {
        return plugin("org.apache.maven.plugins", "maven-" + name + "-plugin", version);
    }


    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The Maven Project basedir.
     *
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    protected File basedir;

    /**
     * @component
     * @required
     */
    protected DelegatingPluginManager pluginManager;

    /**
     * The Maven Session Object
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    protected MavenSession session;


    /**
     * @parameter expression="${serverType}" default-value="tomcat"
     */
    protected String serverType = "tomcat";

    private ServerType serverTypeEnum;

    protected ServerType serverType() {
        if (serverTypeEnum == null) {
            try {
                serverTypeEnum = ServerType.valueOf(serverType.toUpperCase());
                log("Using server type [" + serverTypeEnum + "].");
            } catch (IllegalArgumentException e) {
                log("Unknown server type [" + serverType + "], using UNKNOWN.");
                serverTypeEnum = ServerType.UNKNOWN;
            }
        }
        return serverTypeEnum;
    }

    protected void setArtifactType(File file, String type) {
        log("setting artifact type for " + file + " to " + type);
        project.getArtifact().setFile(file);
        project.getArtifact().setArtifactHandler(new DefaultArtifactHandler(type));
    }

    protected void log(String message) {
        log();
        getLog().info(message);
    }

    protected void log(String message, Throwable cause) {
        log();
        getLog().info(message, cause);
    }

    protected void log() {
        getLog().info("************************");
    }

    protected void warn(String message) {
        getLog().warn("************************");
        getLog().warn(message);
    }


}
