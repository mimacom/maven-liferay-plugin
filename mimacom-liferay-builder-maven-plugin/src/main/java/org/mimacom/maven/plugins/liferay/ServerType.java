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
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.Manifest.Attribute;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum ServerType {
    TOMCAT(true) {
        private final Pattern LIBS_KEY = Pattern.compile("(\\s*common.loader\\s*=)(.*)");

        @Override
        public File serverDeployDirectory(InstallMojo base) {
            return base.deployDirectory != null ? base.deployDirectory : new File(base.serverDirectory, "webapps");
        }

        @Override
        public File serverExplodedDirectory(BaseLiferayMojo base) {
            return new File(base.serverDirectory, "webapps/" + base.finalName);
        }

        @Override
        public File commonLibsDirectory(BaseLiferayMojo base) {
            return base.commonLibsDirectory != null ? base.commonLibsDirectory : new File(base.serverDirectory, "lib/liferay");
        }

        @Override
        public void adjustConfig(CreatePortalMojo base) throws MojoExecutionException {
            File config = new File(base.serverDirectory, "conf/catalina.properties");
            if (config.exists()) {
                try {
                    List<String> lines = new ArrayList<String>();
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(config), "iso-8859-1"));
                    while (in.ready()) {
                        String line = in.readLine();
                        Matcher m = LIBS_KEY.matcher(line);
                        if (m.matches()) {
                            String libs = m.group(2);
                            libs = addIfNecessary(libs, commonLibsDirectory(base).getAbsolutePath() + "/*.jar", base.serverDirectory);
                            libs = addIfNecessary(libs, base.configDirectory().getAbsolutePath(), base.serverDirectory);
                            line = m.group(1) + libs;
                        }
                        lines.add(line);
                    }
                    in.close();
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config), "iso-8859-1"));
                    for (String line : lines) {
                        out.write(line);
                        out.newLine();
                    }
                    out.close();
                } catch (IOException e) {
                    // ignore and do nothing
                }
            }
        }

        @Override
        public File configDirectory(CreatePortalMojo base) {
            return new File(base.serverDirectory, "../config");
        }

        private String addIfNecessary(String path, String toCheck, File baseDir) {
            toCheck = toCheck.replace('\\', '/');
            String baseDirName = baseDir.getAbsolutePath().replace('\\', '/');
            boolean found = path.contains(toCheck);
            if (!found && toCheck.startsWith(baseDirName)) {
                toCheck = "${catalina.home}" + toCheck.substring(baseDirName.length());
                found = path.contains(toCheck);
            }
            return found ? path : path + "," + toCheck;
        }

        @Override
        public String downloadFilename(String version) {
            String tomcatVersion = "";
            if (version.charAt(0) == '5') {
                if (version.equals("5.2.9")) {
                    tomcatVersion = "-6.0.29";
                } else {
                    tomcatVersion = "-6.0";
                }
            }
            return "liferay-portal-tomcat" + tomcatVersion + ".zip";
        }

        @Override
        public String commonLibsGroupId() {
            return ProtoLiferayMojo.LIFERAY_COMMON_GROUP_ID + ".tomcat";
        }

        @Override
        public String serverDirectoryInZip(String version) {
            return version.equals("5.1.2") ? "liferay-portal-*" : "liferay-portal-*/tomcat-*";
        }

    },
    WEBSPHERE(false) {
        @Override
        public Manifest additionalManifest(CreatePortalMojo base) throws MojoExecutionException {
            try {
                String[] libs = new File(base.basedir, base.libDir()).list();
                Manifest mf = new Manifest();
                mf.addConfiguredAttribute(new Attribute("Ignore-Scanning-Archives", StringUtils.join(libs, ",")));
                return mf;
            } catch (ManifestException e) {
                throw new MojoExecutionException("Could not add Ignore-Scanning-Archives Manifest entry", e);
            }
        }

        @Override
        public String serverDirectoryInZip(String version) {
            // TODO Auto-generated method stub
            return null;
        }
    },
    // TODO autodeploy on weblogic?
    WEBLOGIC(false) {
        @Override
        public String serverDirectoryInZip(String version) {
            return null;
        }
    },
    UNKNOWN(true) {
        @Override
        public String serverDirectoryInZip(String version) {
            // TODO Auto-generated method stub
            return null;
        }
    };
    private final boolean autoDeploy;

    private ServerType(boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    @SuppressWarnings("unused")
    public File commonLibsDirectory(BaseLiferayMojo base) {
        return null;
    }

    @SuppressWarnings("unused")
    public File configDirectory(CreatePortalMojo base) {
        return null;
    }

    @SuppressWarnings("unused")
    public File serverDeployDirectory(InstallMojo base) {
        return null;
    }

    @SuppressWarnings("unused")
    public File serverExplodedDirectory(BaseLiferayMojo base) {
        return null;
    }

    @SuppressWarnings("unused")
    public Manifest additionalManifest(CreatePortalMojo base) throws MojoExecutionException {
        return null;
    }

    @SuppressWarnings("unused")
    public void adjustConfig(CreatePortalMojo base) throws MojoExecutionException {
    }

    public boolean isAutoDeploy() {
        return autoDeploy;
    }

    public String downloadFilename(String version) {
        return "liferay-portal.war";
    }

    public String commonLibsGroupId() {
        return ProtoLiferayMojo.LIFERAY_COMMON_GROUP_ID;
    }

    public abstract String serverDirectoryInZip(String version);
}
