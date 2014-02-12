package org.mimacom.commons.liferay.adapter;


import java.io.File;

import org.apache.maven.plugin.logging.Log;


public interface LiferayTools {
    String ROLE = LiferayTools.class.getName();

    String getVersion();

    void initLiferay();

    void mergeCss(File basedir);

    void initLog(Log log);

    void deployLayout(String serverType, File sourceDir) throws Exception;

    void deployTheme(String serverType, File sourceDir) throws Exception;

    void deployPortlet(String version, String serverType, File sourceDir) throws Exception;

    void deployHook(String serverType, File sourceDir) throws Exception;

    void buildService(String fileName, String hbmFileName, String ormFileName,
                      String modelHintsFileName, String springFileName,
                      String springBaseFileName, String springClusterFileName,
                      String springDynamicDataSourceFileName, String springHibernateFileName,
                      String springInfrastructureFileName,
                      String springShardDataSourceFileName, String apiDir, String implDir,
                      String jsonFileName, String remotingFileName, String sqlDir,
                      String sqlFileName, String sqlIndexesFileName,
                      String sqlIndexesPropertiesFileName, String sqlSequencesFileName,
                      boolean autoNamespaceTables, String beanLocatorUtil, String propsUtil,
                      String pluginName, String testDir);
}
