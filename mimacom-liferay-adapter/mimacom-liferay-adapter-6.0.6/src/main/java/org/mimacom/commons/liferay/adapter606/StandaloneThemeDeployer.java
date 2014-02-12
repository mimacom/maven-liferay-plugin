/**
 * 
 */
package org.mimacom.commons.liferay.adapter606;


import java.io.File;
import java.util.Collections;

import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.tools.deploy.ThemeDeployer;


public class StandaloneThemeDeployer extends ThemeDeployer {
    public StandaloneThemeDeployer(String serverType) {
        appServerType = serverType;
        jars = Collections.emptyList();
    }

    @Override
    public void deployDirectory(File srcFile, File mergeDir, File deployDir, String displayName, boolean overwrite,
            PluginPackage pluginPackage) throws Exception {
        super.deployDirectory(srcFile, null, null, displayName, overwrite, pluginPackage);
    }

    @Override
    public void deployFile(File srcFile) throws Exception {
        super.deployFile(srcFile);
    }

    @Override
    protected String getDisplayName(File srcFile) {
        String displayName = srcFile.getName();
        return displayName;
    }

}