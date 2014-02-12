package org.mimacom.commons.liferay.adapter6010;

import java.io.File;
import java.util.Collections;

import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.tools.deploy.HookDeployer;

public class StandaloneHookDeployer extends HookDeployer {
	public StandaloneHookDeployer(String serverType) {
		appServerType = serverType;
		jars = Collections.emptyList();
	}

	/**
	 * @see com.liferay.portal.tools.deploy.HookDeployer#getExtraContent(double,
	 *      java.io.File, java.lang.String)
	 */
	@Override
	protected String getExtraContent(double webXmlVersion, File srcFile,
			String displayName) throws Exception {
		StringBuilder content = new StringBuilder(super.getExtraContent(
				webXmlVersion, srcFile, displayName));
		LiferayToolsImpl.addExtraContent(appServerType, content);
		return super.getExtraContent(webXmlVersion, srcFile, displayName);
	}

	/**
	 * @see com.liferay.portal.tools.deploy.BaseDeployer#getDisplayName(java.io.File)
	 */
	@Override
	protected String getDisplayName(File srcFile) {
		return super.getDisplayName(srcFile);
	}

	/**
	 * overwritten to make it visible
	 * 
	 * @see com.liferay.portal.tools.deploy.BaseDeployer#deployFile(java.io.File)
	 */
	@Override
	public void deployFile(File srcFile) throws Exception {
		super.deployFile(srcFile);
	}

	/**
	 * overwritten to make it visible
	 * 
	 * @see com.liferay.portal.tools.deploy.BaseDeployer#deployDirectory(java.io.File,
	 *      java.io.File, java.io.File, java.lang.String, boolean,
	 *      com.liferay.portal.kernel.plugin.PluginPackage)
	 */
	@Override
	public void deployDirectory(File srcFile, File mergeDir, File deployDir,
			String displayName, boolean overwrite, PluginPackage pluginPackage)
			throws Exception {
		super.deployDirectory(srcFile, null, null, displayName, overwrite,
				pluginPackage);
	}
}
