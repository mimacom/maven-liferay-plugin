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

import org.mimacom.maven.plugins.liferay.RealBaseLiferayMojo;

import java.util.HashMap;
import java.util.Map;

//IMPORTANT: This class and its superclasses must NOT depend on LiferayTools! Not even in the import declaration.
public abstract class AbstractPrepareMojo extends RealBaseLiferayMojo {
    private final static Map<String, String> KNOWN_EE_VERSIONS = new HashMap<String, String>();
    static {
        KNOWN_EE_VERSIONS.put("5.2.5", "5.2-ee-sp1");
        KNOWN_EE_VERSIONS.put("5.2.6", "5.2-ee-sp2");
        KNOWN_EE_VERSIONS.put("5.2.7", "5.2-ee-sp3");
        KNOWN_EE_VERSIONS.put("5.2.8", "5.2-ee-sp4");
        KNOWN_EE_VERSIONS.put("5.2.9", "5.2-ee-sp5");
        KNOWN_EE_VERSIONS.put("6.0.6", "6.0.6-20110225");
        KNOWN_EE_VERSIONS.put("6.0.10", "6.0-ee");
        KNOWN_EE_VERSIONS.put("6.0.11", "6.0-ee-sp1");
    }

    /**
     * The version of liferay to be downloaded, e.g. 6.0.5
     * 
     * @parameter expression="${version}"
     * @required
     */
    @SuppressWarnings({"JavaDoc"})
    protected String version;

    /**
     * The version of liferay to be downloaded, as specified inside the file, e.g. 6.0-ee-sp1. Must match with version configuration
     * parameter.
     * 
     * @parameter expression="${fileVersion}"
     */
    @SuppressWarnings({"JavaDoc"})
    private String fileVersion;

    protected String fileVersion() {
        if (fileVersion == null) {
            fileVersion = KNOWN_EE_VERSIONS.get(version);
            if (fileVersion == null) {
                fileVersion = version;
            }
        }
        return fileVersion;
    }
}
