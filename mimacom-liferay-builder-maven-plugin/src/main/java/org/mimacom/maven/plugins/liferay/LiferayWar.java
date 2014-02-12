package org.mimacom.maven.plugins.liferay;


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
