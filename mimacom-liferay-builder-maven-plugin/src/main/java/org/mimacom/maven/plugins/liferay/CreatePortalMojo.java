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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.edorasframework.tools.common.antfront.impl.Parameters.*;
import static org.edorasframework.tools.common.antfront.impl.Tasks.*;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


/**
 * Create a portal .war-file that can be deployed to a application server.
 *
 * @author stni
 * @requiresDependencyResolution compile
 * @goal createPortal
 */
@SuppressWarnings({"UnusedDeclaration", "JavaDoc"})
public class CreatePortalMojo extends BaseLiferayMojo {
    private static final ArtifactFilter PROVIDED_SCOPE_FILTER = new ArtifactFilter() {
        public boolean include(Artifact artifact) {
            return artifact.getScope() == null || !artifact.getScope().equals("provided");
        }
    };

    private static class SubArtifactCollector implements DependencyNodeVisitor {
        private final DependencyNodeVisitor SUB_ARTIFACT_ADDER = new DependencyNodeVisitor() {
            public boolean visit(DependencyNode node) {
                if (PROVIDED_SCOPE_FILTER.include(node.getArtifact())) {
                    subArtifactIds.add(node.getArtifact().getArtifactId());
                }
                return true;
            }

            public boolean endVisit(DependencyNode node) {
                return true;
            }
        };

        private List<String> artifactIds;

        private List<String> subArtifactIds;

        public SubArtifactCollector(List<String> artifactIds) {
            this.artifactIds = artifactIds;
            subArtifactIds = new ArrayList<String>();
        }

        public List<String> getSubArtifactIds() {
            return subArtifactIds;
        }

        public boolean visit(DependencyNode node) {
            if (artifactIds == null || artifactIds.contains(node.getArtifact().getArtifactId())) {
                node.accept(SUB_ARTIFACT_ADDER);
                return false;
            }
            return true;
        }

        public boolean endVisit(DependencyNode node) {
            return true;
        }
    }

    /**
     * @component
     * @required
     * @readonly
     */
    private DependencyTreeBuilder depTreeBuilder;

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;


    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * @component
     * @required
     * @readonly
     */
    private ArtifactCollector artifactCollector;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepo;

    /**
     * The directory where the config files (src/main/config) should be copied.
     *
     * @parameter expression="${configDirectory}"
     */
    private File configDirectory;

    protected File configDirectory() throws MojoExecutionException {
        return checkRequired("configDirectory", configDirectory != null ? configDirectory : serverType().configDirectory(this));
    }

    /**
     * ArtifactIds of dependencies that should end in the commonLib directory.
     *
     * @parameter expression="${commonArtifactIds}"
     */
    protected String commonArtifactIds;

    /**
     * Jar files that should be copied from WEB-INF/lib to the commonLib directory. (With inner tags &lt;include&gt; and &lt;exclude&gt;)
     *
     * @parameter expression="${copyLibsToCommon}"
     */
    protected IncludeAndExclude copyLibsToCommon = new IncludeAndExclude("commons-logging,liferay-icu4j,saw-api,xalan,xercesImpl,xml-apis",
            null);

    /**
     * Jar files that should be moved from WEB-INF/lib to the commonLib directory. (With inner tags &lt;include&gt; and &lt;exclude&gt;)
     *
     * @parameter expression="${moveLibsToCommon}"
     */
    protected IncludeAndExclude moveLibsToCommon = new IncludeAndExclude(
            "annotations,ccpp,container,portal-kernel,portal-service,portlet,portlet-container,serializer", null);

    /**
     * Jar files in WEB-INF/lib that should end neither in commonLib nor in the war file. (With inner tags &lt;include&gt; and
     * &lt;exclude&gt;)
     *
     * @parameter expression="${deleteLibs}"
     */
    protected IncludeAndExclude deleteLibs;

    /**
     * Files that should be deleted before creating the war file. (With inner tags &lt;include&gt; and &lt;exclude&gt;)
     *
     * @parameter expression="${deleteFiles}"
     */
    protected IncludeAndExclude deleteFiles;


    /**
     * If the common libs should be copied to the local server.
     *
     * @parameter expression="${copyCommonLibs}"
     */
    protected Boolean copyCommonLibs;

    /**
     * The classifier to be used for the configuration artifact.
     *
     * @parameter expression="${configClassifier}" default-value=""
     */
    protected String configClassifier;

    /**
     * The classifier to be used for the common libs artifact.
     *
     * @parameter expression="${commonLibsClassifier}" default-value=""
     */
    protected String commonLibsClassifier;

    /**
     * Flag if the common libs from the maven repository should be used.
     *
     * @parameter expression="${defaultCommonLibs}" default-value="true"
     */
    private Boolean defaultCommonLibs;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (createWar) {
            createWar();
        }

        createAndCopyConfig();
        createAndCopyCommonLibs();
    }

    protected void createAndCopyConfig() throws MojoExecutionException {
        File configDir = new File(basedir, configDir());
        if (!configDir.exists()) {
            log("no config directory (" + configDir() + ")");
        } else {
            log("zipping " + configDir() + " into " + simpleBuildDir() + "config.zip");
            File configZip = new File(buildDir, "config.zip");
            zip(//
                    basedir(configDir),//
                    zipfile(configZip));

            // if no files in src/main/config -> no zip is created
            if (configZip.exists()) {
                mavenHelper.attachArtifact(project, "zip", appendIfNotEmpty("config", configClassifier), configZip);

                if (isCopy(copyConfig)) {
                    log("deleting config in " + configDirectory());
                    delete(dir(configDirectory()), includes("**/*"));

                    log("copying config from " + configDir() + " to " + configDirectory());
                    copy(//
                            fileset(dir(basedir, configDir())),//
                            todir(configDirectory())//
                    );
                }
            }
        }

    }

    protected void createAndCopyCommonLibs() throws MojoExecutionException {
        File commonLibDir = new File(basedir, commonLibDir());
        if (!commonLibDir.exists()) {
            log("no common lib directory (" + commonLibDir() + ")");
        } else {
            log("zipping " + commonLibDir() + " into " + simpleBuildDir() + "commonLib.zip");
            File commonLibZip = new File(buildDir, "commonLib.zip");
            zip(//
                    basedir(commonLibDir),//
                    zipfile(commonLibZip));
            mavenHelper.attachArtifact(project, "zip", appendIfNotEmpty("commonLib", commonLibsClassifier), commonLibZip);

            if (isCopy(copyCommonLibs)) {
                log("adjusting server configuration");
                serverType().adjustConfig(this);

                log("deleting common libs in " + commonLibsDirectory());
                delete(dir(commonLibsDirectory()), includes("**/*"));

                log("copying common libs from " + commonLibDir() + " to " + commonLibsDirectory());
                copy(//
                        fileset(dir(basedir, commonLibDir())),//
                        todir(commonLibsDirectory())//
                );
            }
        }
    }

    private String appendIfNotEmpty(String start, String appendix) {
        if (appendix != null && appendix.length() > 0) {
            return start + "-" + appendix;
        }
        return start;
    }

    protected void createWar() throws MojoExecutionException {
        setArtifactType(artifactWarFile(), "war");

        log("extracting " + liferayWar + " to " + explodedDir());
        unpackLiferayWarArtifact(explodedDir(), null, "war");

        final File exploded = new File(basedir, explodedDir());
        final File libDir = new File(basedir, libDir());

        if (finalNameDir().exists()) {
            log("copying target/" + finalName + " to " + explodedDir());
            copy(//
                    fileset(//
                            dir(finalNameDir()),//
                            excludes(prefixedOriginalConfigs("WEB-INF/classes/"))),//
                    todir(exploded),//
                    overwrite(true)//
            );
        }

        log("copying " + ORIGINAL_CONFIGS + " to " + CONFIG_DIR);
        try {
            expand(//
                    src(libDir, "portal-impl.jar"),//
                    dest(basedir, CONFIG_DIR),//
                    patternset(includes(ORIGINAL_CONFIGS))//
            );
        } catch (Exception e) {
            getLog().info("could not extract " + ORIGINAL_CONFIGS + ": " + e.getMessage());
        }


        String allCommonArtifactIds = "";
        if (commonArtifactIds != null) {
            allCommonArtifactIds = StringUtils.join(getNonProvidedDependencies(commonArtifactIds), ",");
        }
        log("copying project dependencies (without 'provided' scoped dependencies and (" + allCommonArtifactIds + ")) to " + libDir());
        executeMojo(//
                DEPENDENCY_PLUGIN,//
                goal("copy-dependencies"),//
                configuration(//
                        element("outputDirectory", libDir()),//
                        element("excludeScope", "provided"),//
                        element("excludeArtifactIds", allCommonArtifactIds)//
                ),//
                executionEnvironment(project, session, pluginManager)//
        );

        if (allCommonArtifactIds.length() > 0) {
            log("copying common artifacts ((" + allCommonArtifactIds + ") without 'provided' scoped ones) to " + commonLibDir());
            executeMojo(//
                    DEPENDENCY_PLUGIN,//
                    goal("copy-dependencies"),//
                    configuration(//
                            element("outputDirectory", commonLibDir()),//
                            element("excludeScope", "provided"),//
                            element("includeArtifactIds", allCommonArtifactIds)//
                    ),//
                    executionEnvironment(project, session, pluginManager)//
            );
        }

        if (moveLibsToCommon != null && moveLibsToCommon.hasInclude()) {
            String moveInclude = moveLibsToCommon.getIncludeWithSuffixes(".jar");
            String moveExclude = moveLibsToCommon.getExcludeWithSuffixes(".jar");
            log("moving (" + moveInclude + ") without (" + moveExclude + ") from " + libDir() + " to " + commonLibDir());
            move(//
                    fileset(//
                            dir(libDir),//
                            includes(moveInclude),//
                            excludes(moveExclude)),//
                    todir(basedir, commonLibDir()),//
                    failOnError(false)//
            );
        }

        if (copyLibsToCommon != null && copyLibsToCommon.hasInclude()) {
            String copyInclude = copyLibsToCommon.getIncludeWithSuffixes(".jar");
            String copyExclude = copyLibsToCommon.getExcludeWithSuffixes(".jar");
            log("copying (" + copyInclude + ") without (" + copyExclude + ") from " + libDir() + " to " + commonLibDir());
            copy(//
                    fileset(//
                            dir(libDir),//
                            includes(copyInclude),//
                            excludes(copyExclude)),//
                    todir(basedir, commonLibDir()),//
                    failOnError(false)//
            );
        }

        if (defaultCommonLibs != null && defaultCommonLibs) {
            try {
                MavenProject liferayCommonLibs = resolveArtifact(serverType().commonLibsGroupId(), "all", liferayWar.getVersion());
                List<Element> artifacts = new ArrayList<Element>();
                @SuppressWarnings({"unchecked"})
                List<Dependency> dependencies = liferayCommonLibs.getDependencies();
                for (Dependency dep : dependencies) {
                    artifacts.add(element("artifactItem", //
                            element("groupId", dep.getGroupId()),//
                            element("artifactId", dep.getArtifactId()),//
                            element("version", dep.getVersion())));
                }

                log("copying default common libs from to" + commonLibDir());
                executeMojo(//
                        DEPENDENCY_PLUGIN,//
                        goal("copy"),//
                        configuration(//
                                element("outputDirectory", commonLibDir()),//
                                element("artifactItems", artifacts.toArray(new Element[artifacts.size()]))//
                        ),//
                        executionEnvironment(project, session, pluginManager)//
                );
            } catch (Exception e) {
                log("Could not copy default common libs", e);
            }
        }

        if (deleteLibs != null && deleteLibs.hasInclude()) {
            String deleteInclude = deleteLibs.getIncludeWithSuffixes(".jar");
            String deleteExclude = deleteLibs.getExcludeWithSuffixes(".jar");
            log("deleting (" + deleteInclude + ") without (" + deleteExclude + ") from " + libDir());
            delete(//
                    dir(libDir),//
                    includes(deleteInclude),//
                    excludes(deleteExclude)//
            );
        }

        if (deleteFiles != null && deleteFiles.hasInclude()) {
            String deleteInclude = deleteFiles.getIncludeWithSuffixes("");
            String deleteExclude = deleteFiles.getExcludeWithSuffixes("");
            log("deleting (" + deleteInclude + ") without (" + deleteExclude + ") from " + explodedDir());
            delete(//
                    dir(exploded),//
                    includes(deleteInclude),//
                    excludes(deleteExclude)//
            );
        }

        createWar(exploded, serverType().additionalManifest(this));
    }

    List<String> getNonProvidedDependencies(String possibleArtifactIds) throws MojoExecutionException {
        try {
            DependencyNode root = depTreeBuilder.buildDependencyTree(project, localRepo, artifactFactory, artifactMetadataSource,
                    PROVIDED_SCOPE_FILTER, artifactCollector);
            SubArtifactCollector sac = new SubArtifactCollector(possibleArtifactIds == null ? null : Arrays.asList(new IncludeAndExclude(
                    possibleArtifactIds, null).getIncludeSplit()));
            root.accept(sac);
            return sac.getSubArtifactIds();
        } catch (DependencyTreeBuilderException e) {
            throw new MojoExecutionException("error building dependency tree", e);
        }
    }

    /**
     * Normally 'target/exploded'
     *
     * @return
     */
    protected String explodedDir() {
        return simpleBuildDir() + "exploded";
    }

    /**
     * Normally 'target/exploded/WEB-INF/lib'
     *
     * @return
     */
    String libDir() {
        return explodedDir() + "/WEB-INF/lib";
    }
}
