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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum ServerType {
    TOMCAT(true) {
        private final Pattern LIBS_KEY = Pattern.compile("(\\s*common.loader\\s*=)(.*)");

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
        public String serverDirectoryInZip(String version) {
            return version.equals("5.1.2") ? "liferay-portal-*" : "liferay-portal-*/tomcat-*";
        }

    },
    WEBSPHERE(false) {

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


    public boolean isAutoDeploy() {
        return autoDeploy;
    }

    public String downloadFilename(String version) {
        return "liferay-portal.war";
    }

    public String commonLibsGroupId() {
        return RealBaseLiferayMojo.LIFERAY_COMMON_GROUP_ID;
    }

    public abstract String serverDirectoryInZip(String version);
}
