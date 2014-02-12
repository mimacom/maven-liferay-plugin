/**
 * 
 */
package org.mimacom.commons.liferay.adapter528;


import java.io.File;
import java.util.Collections;

import org.mimacom.commons.liferay.adapter528.LiferayToolsImpl;

import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.tools.deploy.LayoutTemplateDeployer;


public class StandaloneLayoutDeployer extends LayoutTemplateDeployer {
    public StandaloneLayoutDeployer(String serverType) {
        appServerType = serverType;
        jars = Collections.emptyList();
    }

    @Override
    public void deployFile(File srcFile) throws Exception {
        super.deployFile(srcFile);
    }

    @Override
    public void deployDirectory(File srcFile, File mergeDir, File deployDir, String displayName, boolean overwrite,
            PluginPackage pluginPackage) throws Exception {
        super.deployDirectory(srcFile, null, null, displayName, overwrite, pluginPackage);
    }

    @Override
    protected String getExtraContent(double webXmlVersion, File srcFile, String displayName) throws Exception {
        StringBuilder content = new StringBuilder(super.getExtraContent(webXmlVersion, srcFile, displayName));

        LiferayToolsImpl.addExtraContent(appServerType, content);
        return content.toString();
    }

    @Override
    protected String getDisplayName(File srcFile) {
        String displayName = srcFile.getName();
        return displayName;
    }
}