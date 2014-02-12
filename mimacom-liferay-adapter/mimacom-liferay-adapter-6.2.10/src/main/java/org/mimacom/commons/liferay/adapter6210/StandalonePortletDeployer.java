/**
 * 
 */
package org.mimacom.commons.liferay.adapter6210;

import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.tools.deploy.PortletDeployer;

import java.io.File;
import java.util.Collections;

public class StandalonePortletDeployer extends PortletDeployer {
	private String version;

	public StandalonePortletDeployer(String version, String serverType) {
		this.version = version;
		appServerType = serverType;
		jars = Collections.emptyList();
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
		String displayName = srcFile.getName() + "-portlet-" + version;
		return displayName;
	}

}