package org.mimacom.maven.plugins.liferay.prepare;


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

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;


/**
 * Download the liferay-portal.web, liferay-portal-src.zip and liferay-portal-dependencies.zip from sourceforge.net or liferay.com
 * 
 * @author stni
 * @goal downloadLiferay
 * @requiresProject false
 */
public class DownloadLiferayMojo extends AbstractPrepareMojo {

    /**
     * The username to be used to log in to liferay.com.
     * 
     * @parameter expression="${username}"
     */
    private String username;

    /**
     * The password to be used to log in to liferay.com.
     * 
     * @parameter expression="${password}"
     */
    private String password;

    /**
     * The mirror for sourceforge to be used.
     * 
     * @parameter expression="${mirror}" default-value="switch"
     */
    private String mirror;

    /**
     * A comma separated list of artifacts to be downloaded, can contain "web", "src", "deps".
     * 
     * @parameter expression="${includes}" default-value="web,src,deps"
     */
    private String includes;

    public void execute() throws MojoExecutionException, MojoFailureException {
        AbstractDownloader loader = null;
        try {
            if (username != null && password != null) {
                log("Trying to download from liferay with username/password");
                if (fileVersion().equals(version)) {
                    log("******* WARN *******");
                    log("You are using username/password to download probably an EE version, but no fileVersion is given. If it does not work, try using the parameter -DfileVersion=<...>");
                }
                loader = new LiferayDownloader(username, password, 5);
            } else {
                log("Trying to download from sourceforge");
                loader = new SourceforgeDownloader(mirror, 5);
            }

            File target = new File(basedir, "target");
            log("Downloading into " + target.getAbsolutePath());
            if (includes.contains("web")) {
                log("downloading portal-web...");
                download(loader, target, serverType().downloadFilename(version), version, fileVersion());
                System.out.println();
            }

            if (includes.contains("src")) {
                log("downloading portal sources...");
                download(loader, target, "liferay-portal-src.zip", version, fileVersion());
                System.out.println();
            }

            if (includes.contains("deps")) {
                log("downloading portal dependencies...");
                download(loader, target, "liferay-portal-dependencies.zip", version, fileVersion());
                System.out.println();
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Problem downloading", e);
        } finally {
            try {
                if (loader != null) {
                    loader.close();
                }
            } catch (IOException e) {
                log("Problem closing downloader: " + e.getMessage());
            }
        }
    }

    private String completeName(String filename, String fileVersion) {
        int pos = filename.lastIndexOf('.');
        return filename.substring(0, pos) + "-" + fileVersion + filename.substring(pos);
    }

    private void download(AbstractDownloader loader, File destDir, String filename, String version, String fileVersion) throws IOException {
        String file = completeName(filename, fileVersion);
        long size = loader.download(destDir, file, version, fileVersion);
        if (size < 1000 * 1000) {
            log("Download was too small, maybe wrong username/password was given");
            String newName = file.substring(0, file.length() - 3) + "html";
            System.out.println(new File(destDir, file));
            new File(destDir, file).renameTo(new File(destDir, newName));
            log("The resulting website was saved to " + newName);
        }
    }

    private static abstract class AbstractDownloader extends MultithreadedDownloader {
        protected AbstractDownloader(int threads) {
            super(threads);
        }

        protected long download(File dest, String url) throws IOException {
            System.out.println("Downloading " + url);
            final String back = StringUtils.repeat("\u0008", 25);
            return download(url, dest, 100000, new ProgressObserver() {
                public void notify(Counter counter) {
                    String out = String.format("%.2f MB at %.2f MB/s", counter.getTotal() / 1024f / 1024,
                        counter.getBytesPerSecond() / 1024f / 1024);
                    System.out.printf(back + out);
                }
            });
        }

        public abstract long download(File destDir, String filename, String version, String fileVersion) throws IOException;
    }

    private static class SourceforgeDownloader extends AbstractDownloader {
        private final static String SOURCE_FORGE = "http://downloads.sourceforge.net/project/lportal/Liferay%20Portal/";

        private final String mirror;

        public SourceforgeDownloader(String mirror, int threads) {
            super(threads);
            this.mirror = mirror;
        }

        public long download(File destDir, String filename, String version, String fileVersion) throws IOException {
            return download(new File(destDir, filename), SOURCE_FORGE + version + "/" + filename + "?use_mirror=" + mirror);
        }
    }

    private static class LiferayDownloader extends AbstractDownloader {
        private final static String LIFERAY_HOME = "https://www.liferay.com/";

        public LiferayDownloader(String username, String password, int threads) throws IOException {
            super(threads);
            loginPage();
            login(username, password);
        }

        private void loginPage() throws IOException {
            GetMethod loginPage = new GetMethod(
                    LIFERAY_HOME
                            + "de/home?p_p_id=58&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&saveLastPath=0&_58_struts_action=%2Flogin%2Flogin");
            try {
                int status = httpClient.executeMethod(loginPage);
                if (status != HttpStatus.SC_OK) {
                    throw new HttpException("Login page (" + loginPage.getURI() + ") could not be load, status: " + status);
                }
            } finally {
                loginPage.releaseConnection();
            }
        }

        private void login(String username, String password) throws IOException {
            PostMethod login = new PostMethod(
                    LIFERAY_HOME
                            + "home?p_auth=0vfVPdDF&p_p_id=58&p_p_lifecycle=1&p_p_state=maximized&p_p_mode=view&saveLastPath=0&_58_struts_action=%2Flogin%2Flogin");
            try {
                NameValuePair[] params = new NameValuePair[] { new NameValuePair("_58_login", username),
                        new NameValuePair("_58_password", password) };
                login.setRequestBody(params);
                int status = httpClient.executeMethod(login);
                status = followRedirect(login, status);
                if (status != HttpStatus.SC_OK) {
                    throw new HttpException("Could not log in (" + login.getURI() + "), status: " + status);
                }
            } finally {
                login.releaseConnection();
            }
        }

        private int followRedirect(HttpMethod method, int status) throws IOException {
            if (status != HttpStatus.SC_MOVED_TEMPORARILY) {
                return status;
            }
            GetMethod after = new GetMethod(method.getResponseHeader("Location").getValue());
            try {
                return httpClient.executeMethod(after);
            } finally {
                after.releaseConnection();
            }
        }


        public long download(File destDir, String filename, String version, String fileVersion) throws IOException {
            return download(
                new File(destDir, filename),
                LIFERAY_HOME
                        + "group/customer/downloads?p_p_id=3_WAR_osbportlet&p_p_lifecycle=1&p_p_state=maximized&_3_WAR_osbportlet_fileName=/portal/"
                        + version + "/" + filename);
        }

        @Override
        public void close() throws IOException {
            try {
                logout();
            } finally {
                super.close();
            }
        }

        private void logout() throws IOException {
            GetMethod logout = new GetMethod(LIFERAY_HOME + "c/portal/logout");
            try {
                int status = httpClient.executeMethod(logout);
                if (status != HttpStatus.SC_OK) {
                    throw new HttpException("Could not log out, status: " + status);
                }
            } finally {
                logout.releaseConnection();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        AbstractDownloader m;
        m = new LiferayDownloader("replace with liferay.com user", "replace with liferay.com password", 4);
        m.download(new File("c:/temp"), "liferay-portal-src.zip", "6.0.11", "6.0-ee-sp1");
        m.close();
        m = new SourceforgeDownloader("switch", 2);
        m.download(new File("c:/temp"), "liferay-portal-src.zip", "6.0.5", "6.0.5");
        m.close();
    }
}
