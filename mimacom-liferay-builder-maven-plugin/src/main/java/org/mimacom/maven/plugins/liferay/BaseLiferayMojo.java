package org.mimacom.maven.plugins.liferay;


import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.Manifest.Attribute;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

import static org.edorasframework.tools.common.antfront.impl.Parameters.basedir;
import static org.edorasframework.tools.common.antfront.impl.Parameters.destFile;
import static org.edorasframework.tools.common.antfront.impl.Parameters.manifest;
import static org.edorasframework.tools.common.antfront.impl.Parameters.webxml;
import static org.edorasframework.tools.common.antfront.impl.Tasks.war;


@SuppressWarnings({"UnusedDeclaration", "JavaDoc"})
public abstract class BaseLiferayMojo extends ProtoLiferayMojo {
    private static final Pattern CONTAINS_NULL = Pattern.compile("\\Wnull\\W");

    protected final static String ORIGINAL_CONFIGS = "portal.properties,META-INF/portal-log4j.xml";

    protected static final String RESOURCES_DIR = "src/main/resources";

    protected static final String WEBAPP_DIR = "src/main/webapp";

    protected static final String CONFIG_DIR = "src/main/config";

    protected String prefixedOriginalConfigs(String prefix) {
        return prefix + ORIGINAL_CONFIGS.replace(",", "," + prefix);
    }

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    private ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     */
    private ArtifactResolver resolver;

    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    private List<ArtifactRepository> remoteRepos;

    /**
     * @component
     * @required
     * @readonly
     */
    private MavenProjectBuilder projectBuilder;

    /**
     * The directory where the application server is installed.
     *
     * @parameter expression="${serverDirectory}"
     */
    protected File serverDirectory;

    /**
     * The liferay base directory. If non is given, ${serverDirectory}/.. is assumed.
     *
     * @parameter expression="${liferayDirectory}"
     */
    protected File liferayDirectory;

    protected File liferayDirectory() throws MojoExecutionException {
        if (liferayDirectory != null) {
            return liferayDirectory;
        }
        return checkRequired("serverDirectory", serverDirectory).getParentFile();
    }

    /**
     * The common lib directory of the locally used server. This is set with a sensible default value depending on the server type.
     *
     * @parameter expression="${commonLibsDirectory}"
     */
    protected File commonLibsDirectory;

    protected File commonLibsDirectory() throws MojoExecutionException {
        if (commonLibsDirectory == null) {
            commonLibsDirectory = serverType().commonLibsDirectory(this);
        }
        return check("commonLibsDirectory", commonLibsDirectory);
    }

    /**
     * The artifact (containing groupId, artifactId, version) of the liferay war file to be used as base.
     *
     * @parameter expression="${liferayWar}"
     */
    protected LiferayWar liferayWar;

    /**
     * If the config files should be copied to the server config directory.
     *
     * @parameter expression="${copyConfig}"
     */
    protected Boolean copyConfig;

    /**
     * The default value for the parameters 'copyDeploy', 'copyConfig' and 'copyCommonLibs'
     *
     * @parameter expression="${localCopy}"
     */
    protected Boolean localCopy;

    /**
     * If the war file containing the portal should be created. If false, only common libs and config files are created.
     *
     * @parameter expression="${createWar}" default-value="true"
     */
    protected boolean createWar;

    protected boolean isCopy(Boolean value) {
        if (value != null) {
            return value;
        }
        if (localCopy != null) {
            return localCopy;
        }
        return serverType().isAutoDeploy();
    }

    /**
     * Normally 'target/config'
     *
     * @return
     */
    protected String configDir() {
        return simpleBuildDir() + "config";
    }

    protected File checkRequired(String parameter, File file) throws MojoExecutionException {
        if (file == null) {
            log("One or more required plugin parameters are invalid/missing for 'maven-liferay-plugin'\n" + //
                    "Inside the definition for plugin 'mimacom-liferay-builder-maven-plugin' specify the following:\n\n" + //
                    "<configuration>\n" + //
                    "  ...\n" + //
                    "  <" + parameter + ">VALUE</" + parameter + ">\n" + //
                    "</configuration>\n\n" + //
                    "-OR-\n\n" + //
                    "on the command line, specify: '-D" + parameter + "=VALUE'");
            throw new MojoExecutionException("Missing parameter '" + parameter + "'");
        }
        return check(parameter, file);
    }

    protected File check(String parameter, File file) throws MojoExecutionException {
        if (file != null && CONTAINS_NULL.matcher(file.getAbsolutePath()).find()) {
            log("The the parameter '" + parameter
                    + "' of the plugin 'org.edorasframework.tools.maven.liferay' is probably invalid. It contains the string 'null'.\n" + //
                    "Inside the definition for plugin 'org.edorasframework.tools.maven.liferay', the parameter is specified by:\n\n" + //
                    "<configuration>\n" + //
                    "  ...\n" + //
                    "  <" + parameter + ">" + file.getAbsolutePath() + "/" + parameter + ">\n" + //
                    "</configuration>\n\n" + //
                    "-OR-\n\n" + //
                    "on the command line: '-D" + parameter + "=" + file.getAbsolutePath() + "'");
            throw new MojoExecutionException("Invalid file parameter '" + parameter + "'");
        }
        return file;
    }

    /**
     * Normally 'target/commonLib'
     *
     * @return
     */
    protected String commonLibDir() {
        return simpleBuildDir() + "commonLib";
    }

    protected MavenProject resolveArtifact(String groupId, String artifactId, String version) throws ArtifactResolutionException, ArtifactNotFoundException, ProjectBuildingException {
        Artifact artifact = factory.createProjectArtifact(groupId, artifactId, version);
        resolver.resolve(artifact, remoteRepos, local);
        return projectBuilder.build(artifact.getFile(), local, null);
    }

    protected void unpackLiferayWarArtifact(String outputDirectory, String classifier, String type) throws MojoExecutionException {
        executeMojo(//
                DEPENDENCY_PLUGIN,//
                goal("unpack"),//
                configuration(//
                        element("outputDirectory", outputDirectory),//
                        element("artifactItems", liferayArtifactItemElement(classifier, type))//
                ),//
                executionEnvironment(project, session, pluginManager)//
        );

    }

    protected Element liferayArtifactItemElement(String classifier, String type) {
        Element[] args = new Element[]{//
                element(//
                        name("groupId"), liferayWar.getGroupId()),//
                element("artifactId", liferayWar.getArtifactId()),//
                element("version", liferayWar.getVersion(tools)),//
                element("type", type), //
                element("classifier", classifier) //
        };
        return element(name("artifactItem"), args);
    }

    // executeMojo(//
    // plugin("org.apache.maven.plugins", "maven-war-plugin"),//
    // goal("manifest"),//
    // configuration(//
    // element(//
    // name("warSourceDirectory"), explodedDir()) //
    // ),//
    // executionEnvironment(project, session, pluginManager)//
    // );
    // the following try-catch-block is only because of http://jira.codehaus.org/browse/MWAR-197
    protected void createWar(File basedir, Manifest additional) throws MojoExecutionException {
        try {
            File mfFile = new File(basedir, "META-INF/MANIFEST.MF");
            Manifest mf;
            if (mfFile.exists()) {
                mf = new Manifest(new InputStreamReader(new FileInputStream(mfFile), "UTF-8"));
            } else {
                mfFile.getParentFile().mkdirs();
                mf = new Manifest();
            }
            if (additional != null) {
                mf.merge(additional);
            }
            Plugin warPlugin = getPlugin("maven-war-plugin");
            if (warPlugin != null && warPlugin.getConfiguration() != null) {
                PlexusConfiguration conf = new XmlPlexusConfiguration((Xpp3Dom) warPlugin.getConfiguration());
                PlexusConfiguration[] entries = conf.getChild("archive").getChild("manifestEntries").getChildren();
                for (PlexusConfiguration entry : entries) {
                    Attribute existing = mf.getMainSection().getAttribute(entry.getName());
                    if (existing != null) {
                        if (!existing.getValue().equals(entry.getValue())) {
                            log("Manifest entry [" + entry.getName() + "] in pom.xml overwrites the value defined in META-INF/MANIFEST.MF");
                            existing.setValue(entry.getValue());
                        }
                    } else {
                        mf.addConfiguredAttribute(new Attribute(entry.getName(), entry.getValue()));
                    }
                }
            }
            PrintWriter out = new PrintWriter(mfFile);
            mf.write(out);
            out.close();

            log("creating " + finalName + ".war from " + basedir);
            war(//
                    basedir(basedir),//
                    webxml(basedir, "WEB-INF/web.xml"),//
                    manifest(mfFile),//
                    destFile(artifactWarFile())//
            );
        } catch (Exception e) {
            throw new MojoExecutionException("error creating MANIFEST.MF", e);
        }
    }

    protected Plugin getPlugin(String artifactId) {
        @SuppressWarnings("unchecked")
        List<Plugin> plugins = project.getBuild().getPlugins();
        for (Plugin plugin : plugins) {
            if (plugin.getArtifactId().equals(artifactId)) {
                return plugin;
            }
        }
        return null;
    }

    protected boolean isPortalProject(MavenProject project) {
        return project.getPackaging().equals("liferayPortal");
    }

    protected Resource getOrCreateResource(String directory) throws MojoFailureException {
        Resource resource = findResourceByDirectory(directory);
        if (resource == null) {
            resource = new Resource();
            project.getBuild().addResource(resource);
            resource.setDirectory(directory);
        }
        return resource;
    }

    protected Resource findResourceByDirectory(String dir) throws MojoFailureException {
        Resource res = null;
        List<Resource> resources = project.getBuild().getResources();
        for (Resource resource : resources) {
            if (resource.getDirectory().replace('\\', '/').toLowerCase().endsWith(dir)) {
                if (res == null) {
                    res = resource;
                } else {
                    throw new MojoFailureException("Multiple resources with directory " + dir + " found. Don't know what to do...");
                }
            }
        }
        return res;
    }


}
