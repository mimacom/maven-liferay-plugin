package org.mimacom.commons.liferay.adapter523;


import java.io.File;

import org.mimacom.commons.liferay.adapter.LiferayTools;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactory;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.model.ModelHintsImpl;
import com.liferay.portal.model.ModelHintsUtil;
import com.liferay.portal.tools.servicebuilder.ServiceBuilder;
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
            content.append("<param-name>com.ibm.websphere.portletcontainer.PortletDeploymentEnabled</param-name>");
            content.append("<param-value>false</param-value>");
            content.append("</context-param>");
        }
    }

    public String getVersion() {
        return "5.2.3";
    }

    public void initLiferay() {
        new FileUtil().setFile(new FileImpl());
        new SAXReaderUtil().setSAXReader(new SAXReaderImpl());
        new PortalUtil().setPortal(new PortalImpl());
        new HtmlUtil().setHtml(new HtmlImpl());
        ModelHintsImpl modelHints = new ModelHintsImpl();
        modelHints.afterPropertiesSet();
        new ModelHintsUtil().setModelHints(modelHints);
    }

    public void mergeCss(File basedir) {
        // TODO stni
        // create the merged css uncompressed and compressed
        // String cssPath = new File(basedir, "css").getAbsolutePath();
        // String unpacked = cssPath + "/everything_unpacked.css";
        // new CSSBuilder(cssPath, unpacked);
        // YUICompressor.main(new String[] { "--type", "css", "-o", cssPath + "/everything_packed.css", unpacked });
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

    public void deployLayout(String serverType, File sourceDir) throws Exception {
        new StandaloneLayoutDeployer(serverType).deployFile(sourceDir);
    }

    public void deployPortlet(String version, String serverType, File sourceDir) throws Exception {
        new StandalonePortletDeployer(version, serverType).deployFile(sourceDir);
    }

    public void deployTheme(String serverType, File sourceDir) throws Exception {
        new StandaloneThemeDeployer(serverType).deployFile(sourceDir);
    }

    public void deployHook(String serverType, File sourceDir) throws Exception {
        new StandaloneHookDeployer(serverType).deployFile(sourceDir);
    }

    public void buildService(String fileName, String hbmFileName, String ormFileName, String modelHintsFileName, String springFileName,
                             String springBaseFileName, String springClusterFileName, String springDynamicDataSourceFileName,
                             String springHibernateFileName, String springInfrastructureFileName, String springShardDataSourceFileName,
                             String apiDir, String implDir, String jsonFileName, String remotingFileName, String sqlDir, String sqlFileName,
                             String sqlIndexesFileName, String sqlIndexesPropertiesFileName, String sqlSequencesFileName, boolean autoNamespaceTables,
                             String beanLocatorUtil, String propsUtil, String pluginName, String testDir) {
        //no ormFileName,springClusterFileName,springShardDataSourceFileName,
        new ServiceBuilder(fileName, hbmFileName, modelHintsFileName, springFileName,
                springBaseFileName, springDynamicDataSourceFileName,
                springHibernateFileName, springInfrastructureFileName,
                apiDir, implDir, jsonFileName, remotingFileName, sqlDir, sqlFileName,
                sqlIndexesFileName, sqlIndexesPropertiesFileName, sqlSequencesFileName, autoNamespaceTables,
                beanLocatorUtil, propsUtil, pluginName, testDir);
    }
}
