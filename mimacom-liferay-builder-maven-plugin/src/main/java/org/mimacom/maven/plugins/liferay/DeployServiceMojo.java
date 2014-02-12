package org.mimacom.maven.plugins.liferay;


import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * @author nidi
 * @goal deployService
 */
public class DeployServiceMojo extends BaseLiferayMojo {
    /**
     * The ID of the repository to deploy to.
     *
     * @parameter expression="${project.distributionManagement.repository}"
     * @required
     */
    private DeploymentRepository repository;

    /**
     * The ID of the repository to deploy to.
     *
     * @parameter expression="${project.distributionManagement.snapshotRepository}"
     * @required
     */
    private DeploymentRepository snapshotRepository;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isServiceProject()) {
            File apiPom = new File(basedir, "src/api/pom.xml");
            deployApiProject(apiPom);
        }
    }

    private void deployApiProject(File apiPom) throws MojoExecutionException {
        boolean snapshot = project.getVersion().endsWith("-SNAPSHOT");
        DeploymentRepository repo = snapshot ? snapshotRepository : repository;

        try {
            executeMojo(//
                    DEPLOY_PLUGIN,//
                    goal("deploy-file"),//
                    configuration(//
                            element("pomFile", apiPom.getAbsolutePath()),//
                            element("file", apiArtifactJarFile().getAbsolutePath()),//
                            element("version", project.getVersion()),//
                            element("repositoryId", repo.getId()),//
                            element("url", repo.getUrl()),//
                            element("repositoryLayout", repo.getLayout()),//
                            element("uniqueVersion", "" + repo.isUniqueVersion())//
                    ),//
                    executionEnvironment(project, session, pluginManager)//
            );
        } catch (MojoExecutionException e) {
            throw new MojoExecutionException("Problem deploying the api.jar, maybe src/api/pom.xml is invalid", e.getCause());
        }
    }
}
