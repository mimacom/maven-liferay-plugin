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

import org.apache.maven.plugin.dependency.fromConfiguration.ArtifactItem;
import org.mimacom.commons.liferay.adapter.LiferayTools;


public class LiferayWar extends ArtifactItem {
    public LiferayWar() {
        setGroupId("com.liferay.portal");
        setArtifactId("portal-web-with-dependencies");
    }

    public String getVersion(LiferayTools tools) {
        if (getVersion() == null) {
            setVersion(tools.getVersion());
        }
        return getVersion();
    }
}
