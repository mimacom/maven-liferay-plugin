/**
 * 
 */
package org.mimacom.commons.liferay.adapter6110;

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
		super.deployFile(srcFile, specifiedContext);
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