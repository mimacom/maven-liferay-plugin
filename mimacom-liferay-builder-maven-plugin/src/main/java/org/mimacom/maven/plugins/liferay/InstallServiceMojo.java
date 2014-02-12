package org.mimacom.maven.plugins.liferay;


import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;

import static org.edorasframework.tools.common.antfront.impl.Parameters.file;
import static org.edorasframework.tools.common.antfront.impl.Parameters.overwrite;
import static org.edorasframework.tools.common.antfront.impl.Parameters.todir;
import static org.edorasframework.tools.common.antfront.impl.Tasks.copy;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * @author "Marc Brugger"
 * @goal installService
 */
public class InstallServiceMojo extends BaseLiferayMojo {
    /**
     * If the common libs should be copied to the local server.
     *
     * @parameter expression="${copyCommonLibs}"
     */
    protected Boolean copyCommonLibs;

    /**
     * @component
     * @required
     * @readonly
     */
    private MavenProjectBuilder projectBuilder;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepo;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isServiceApiProject() && isCopy(copyCommonLibs)) {
            copyJarAndDependencies(artifactJarFile(), project);
        }
        if (isServiceProject()) {
            File apiPom = generatePomForApiProject();
            installApiProject(apiPom);

            if (isCopy(copyCommonLibs)) {
                try {
                    MavenProject apiProject = projectBuilder.buildWithDependencies(apiPom, localRepo, null);
                    copyJarAndDependencies(apiArtifactJarFile(), apiProject);
                } catch (Exception e) {
                    log("Error reading src/api/pom.xml", e);
                }
            }
        }
    }

    private void installApiProject(File apiPom) throws MojoExecutionException {
        try {
            executeMojo(//
                    INSTALL_PLUGIN,//
                    goal("install-file"),//
                    configuration(//
                            element("file", apiArtifactJarFile().getAbsolutePath()),//
                            element("version", project.getVersion()),//
                            element("pomFile", apiPom.getAbsolutePath())//
                    ),//
                    executionEnvironment(project, session, pluginManager)//
            );
        } catch (MojoExecutionException e) {
            throw new MojoExecutionException("Problem installing the api.jar, maybe src/api/pom.xml is invalid", e.getCause());
        }
    }

    private File generatePomForApiProject() throws MojoExecutionException {
        File apiPom = new File(basedir, "src/api/pom.xml");
        if (!apiPom.exists()) {
            log("Generating a pom.xml for the API");
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(apiPom), "utf-8"));
                writeln(out, "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
                writeln(out, "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">");
                writeln(out, "    <modelVersion>4.0.0</modelVersion>");
                writeln(out, "    <groupId>" + project.getGroupId() + "</groupId>");
                writeln(out, "    <artifactId>" + project.getArtifactId() + "-api</artifactId>");
                writeln(out, "    <version>taken from impl</version>");
                writeln(out, "</project>");
                out.close();
            } catch (IOException e) {
                throw new MojoExecutionException("Problem generating pom.xml for the API", e);
            }
        }
        return apiPom;
    }


    private void writeln(BufferedWriter out, String s) throws IOException {
        out.write(s);
        out.newLine();
    }

    private void copyJarAndDependencies(File jar, MavenProject project) throws MojoExecutionException {
        getLog().info("copying " + jar.getName() + " to " + commonLibsDirectory());
        copy(//
                file(jar),//
                todir(commonLibsDirectory()),//
                overwrite(true)//
        );
        @SuppressWarnings("unchecked")
        Set<Artifact> dependencies = project.getArtifacts();
        for (Artifact dependency : dependencies) {
            if (dependency.getType().equals("jar") && !dependency.getScope().equals("provided")) {
                log("copying common service lib " + dependency.getFile().getName() + " to " + commonLibsDirectory());
                copy(//
                        file(dependency.getFile()),//
                        todir(commonLibsDirectory()),//
                        overwrite(true)//
                );
            }
        }
    }
}
