package org.mimacom.maven.plugins.liferay;


import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.edorasframework.tools.maven.util.DefaultSiteCreator;
import org.edorasframework.tools.maven.util.SiteCreator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * @author stni
 * @goal findWebPatches
 * 
 */
@SuppressWarnings({"JavaDoc"})
public class WebPatchFinderMojo extends AbstractMavenReport {

    /**
     * The Maven Project Object
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * 
     * @component
     * @required
     */
    private SiteRenderer siteRenderer;

    /**
     * Target folder
     *
     * @parameter expression="${project.build.directory}"
     * @readonly
     */
    private File outputDirectory;
    /**
     * basedir
     *
     * @parameter expression="${basedir}"
     * @readonly
     */
    private File basedir;

    /**
     * A comma separated list of directories that should be checked for patches.
     * 
     * @parameter expression="${includes}"
     */
    private String includes;

    /**
     * A comma separated list of directories that should be excluded from checking for patches.
     *
     * @parameter expression="${excludes}"
     */
    private String excludes;

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        Lister cl = new Lister();
        if (includes != null) {
            String[] incs = includes.split(",");
            for (String inc : incs) {
                if (inc.trim().length() > 0) {
                    cl.addInclude(inc.trim());
                }
            }
        }
        if (excludes != null) {
            String[] excs = excludes.split(",");
            for (String exc : excs) {
                if (exc.trim().length() > 0) {
                    cl.addExclude(exc.trim());
                }
            }
        }
        try {
//TODO use portal-web.war
            cl.analyzeJarFiles("html",new File(outputDirectory, project.getBuild().getFinalName() + ".war"));
            cl.analyzeFiles(new File(basedir,"src/main/webapps"),"html");
            Map<String, List<String>> patches = new TreeMap<String, List<String>>();
            Map<String, List<String>> list = cl.getResult().getFiles();
            for (Entry<String, List<String>> e : list.entrySet()) {
                if (e.getValue().size() > 1) {
                    patches.put(e.getKey(), e.getValue());
                }
            }
            SiteCreator siteCreator = new DefaultSiteCreator(getSink());
            Sites.getInstance().patchesSite(siteCreator,"Web Patches", "This table shows all web resources that are defined more than once (i.e. patches)", patches);
        } catch (IOException e) {
            throw new MavenReportException("Problem finding web patches", e);
        }
    }

    public String getDescription(Locale locale) {
        return "Find web resources that are defined more than once";
    }

    public String getName(Locale locale) {
        return "WebPatchFinder";
    }

    public String getOutputName() {
        return "webPatchFinder";
    }

    @Override
    protected String getOutputDirectory() {
        return outputDirectory.toString();
    }

    @Override
    protected MavenProject getProject() {
        return project;
    }

    @Override
    protected SiteRenderer getSiteRenderer() {
        return siteRenderer;
    }

}
