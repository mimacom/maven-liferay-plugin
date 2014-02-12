package org.mimacom.commons.liferay.adapter6110;

import java.io.File;

import org.mimacom.commons.liferay.adapter.LiferayTools;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactory;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.HtmlImpl;
import com.liferay.portal.util.PortalImpl;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.xml.SAXReaderImpl;

public class LiferayToolsImpl implements LiferayTools {
	public LiferayToolsImpl() {
	}

	static void addExtraContent(String appServerType, StringBuilder content) {
		if (ServerDetector.WEBSPHERE_ID.equals(appServerType)) {
			content.append("<context-param>");
			content
					.append("<param-name>com.ibm.websphere.portletcontainer.PortletDeploymentEnabled</param-name>");
			content.append("<param-value>false</param-value>");
			content.append("</context-param>");
		}
	}

	public String getVersion() {
		return "6.1.10";
	}

	public void initLiferay() {
		new FileUtil().setFile(new FileImpl());
		new SAXReaderUtil().setSAXReader(new SAXReaderImpl());
		new PortalUtil().setPortal(new PortalImpl());
		new HtmlUtil().setHtml(new HtmlImpl());
	}

	public void mergeCss(File basedir) {
		// TODO stni
		// create the merged css uncompressed and compressed
		// String cssPath = new File(basedir, "css").getAbsolutePath();
		// String unpacked = cssPath + "/everything_unpacked.css";
		// new CSSBuilder(cssPath, unpacked);
		// YUICompressor.main(new String[] { "--type", "css", "-o", cssPath +
		// "/everything_packed.css", unpacked });
	}

	public void initLog(final org.apache.maven.plugin.logging.Log log) {
		LogFactoryUtil.setLogFactory(new LogFactory() {
			public Log getLog(String name) {
				return new MavenLiferayLog(name, log);
			}

			public Log getLog(Class<?> c) {
				return new MavenLiferayLog(c.getSimpleName(), log);
			}
		});
	}

	public void deployLayout(String serverType, File sourceDir)
			throws Exception {
		new StandaloneLayoutDeployer(serverType).deployFile(sourceDir,null);
	}

	public void deployPortlet(String version, String serverType, File sourceDir)
			throws Exception {
		new StandalonePortletDeployer(version, serverType).deployFile(
				sourceDir, null);
	}

	public void deployTheme(String serverType, File sourceDir) throws Exception {
		new StandaloneThemeDeployer(serverType).deployFile(sourceDir,null);
	}

	public void deployHook(String serverType, File sourceDir) throws Exception {
		new StandaloneHookDeployer(serverType).deployFile(sourceDir,null);
	}

	public void buildService(String arg0, String arg1, String arg2,
			String arg3, String arg4, String arg5, String arg6, String arg7,
			String arg8, String arg9, String arg10, String arg11, String arg12,
			String arg13, String arg14, String arg15, String arg16,
			String arg17, String arg18, String arg19, boolean arg20,
			String arg21, String arg22, String arg23, String arg24) {
		// TODO Auto-generated method stub
		
	}

}
