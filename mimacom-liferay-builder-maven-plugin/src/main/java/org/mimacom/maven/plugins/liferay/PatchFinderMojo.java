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
 * @goal findPatches
 * 
 */
public class PatchFinderMojo extends AbstractMavenReport {

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
     * A comma separated list of package names that should be checked for patches. E.g. 'com/liferay/' would include all liferay packages.
     * 
     * @parameter expression="${includes}" default-value="com.liferay"
     */
    private String includes;

    /**
     * A comma separated list of package names that should be excluded from checking for patches. E.g. 'java/' would exclude all java
     * packages.
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
            cl.analyzeWarClasses(new File(outputDirectory, project.getBuild().getFinalName() + ".war"));
            cl.analyzeZipWithJarClasses(new File(outputDirectory, "commonLib.zip"));
            Map<String, List<String>> patches = new TreeMap<String, List<String>>();
            Map<String, List<String>> list = cl.getResult().getFiles();
            for (Entry<String, List<String>> e : list.entrySet()) {
                if (e.getValue().size() > 1) {
                    patches.put(e.getKey(), e.getValue());
                }
            }
            SiteCreator siteCreator = new DefaultSiteCreator(getSink());
            Sites.getInstance().patchesSite(siteCreator,"Patches", "This table shows all classes that are defined more than once (i.e. patches)", patches);
        } catch (IOException e) {
            throw new MavenReportException("Problem finding patches", e);
        }
    }

    public String getDescription(Locale locale) {
        return "Find classes that are defined more than once";
    }

    public String getName(Locale locale) {
        return "PatchFinder";
    }

    public String getOutputName() {
        return "patchFinder";
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
