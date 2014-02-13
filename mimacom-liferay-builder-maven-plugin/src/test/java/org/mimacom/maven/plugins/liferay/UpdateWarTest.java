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

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.junit.Ignore;
import org.junit.Test;
import org.mimacom.commons.liferay.adapter620cega1.LiferayToolsImpl;

import java.io.File;


public class UpdateWarTest {
    @Test
    @Ignore
    public void testLayout() throws Exception {
        CreateWarMojo uw = new CreateWarMojo();
        uw.basedir = new File("../bitpoc-layout");
        uw.finalName = "bitpoc-layout-0.0.1-SNAPSHOT";
        uw.execute();
    }

    @Test
    @Ignore
    public void testPortlet() throws Exception {
        CreateWarMojo uw = new CreateWarMojo();
        uw.setLog(new SystemStreamLog());
        uw.basedir = new File("../bitpoc-portlet");
        uw.finalName = "bitpoc";
        uw.project = new MavenProject();
        uw.project.setVersion("1.0.1");
        uw.tools = new LiferayToolsImpl();
        uw.execute();
    }

    @Test
    @Ignore
    public void testTheme() throws Exception {
        CreateWarMojo uw = new CreateWarMojo();
        uw.basedir = new File("../bitpoc-theme");
        uw.finalName = "bitpoc-theme-0.0.1-SNAPSHOT";
        uw.execute();
    }

    @Test
    @Ignore
    public void testCssBuilder() {
    // new FileUtil().setFile(new FileImpl());
    // String cssPath = new File("../bitpoc-theme/target/bitpoc-theme-0.0.1-SNAPSHOT/css").getAbsolutePath();
    // new CSSBuilder(cssPath, cssPath + "/everything_unpacked.css");
    // YUICompressor.main(new String[] { "--type", "css", "-o", cssPath + "/everything_packed.css",
    // cssPath + "/everything_unpacked.css" });
    }

    public static void main(String[] args) {
        new UpdateWarTest().testCssBuilder();
    }
}
