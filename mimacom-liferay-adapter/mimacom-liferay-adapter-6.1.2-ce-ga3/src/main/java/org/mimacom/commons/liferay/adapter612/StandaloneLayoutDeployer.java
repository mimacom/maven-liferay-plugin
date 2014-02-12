/**
 * 
 */
package org.mimacom.commons.liferay.adapter612;

import java.io.File;
import java.util.Collections;

import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.tools.deploy.LayoutTemplateDeployer;

public class StandaloneLayoutDeployer extends LayoutTemplateDeployer {
	public StandaloneLayoutDeployer(String serverType) {
		appServerType = serverType;
		jars = Collections.emptyList();
	}

	public void deployFile(File srcFile, String specifiedContext)
			throws Exception {
		super.deployFile(srcFile, srcFile, srcFile, specifiedContext, unpackWar, null);
	}

	@Override
	public void deployDirectory(File srcFile, File mergeDir, File deployDir,
			String displayName, boolean overwrite, PluginPackage pluginPackage)
			throws Exception {
		super.deployDirectory(srcFile, null, null, displayName, overwrite,
				pluginPackage);
	}

	@Override
	public String getExtraContent(double webXmlVersion, File srcFile,
			String displayName) throws Exception {
		StringBuilder content = new StringBuilder(super.getExtraContent(
				webXmlVersion, srcFile, displayName));

		LiferayToolsImpl.addExtraContent(appServerType, content);
		return content.toString();
	}

	@Override
	public String getDisplayName(File srcFile) {
		String displayName = srcFile.getName();
		return displayName;
	}
}