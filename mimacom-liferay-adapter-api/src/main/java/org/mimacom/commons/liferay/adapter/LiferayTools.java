package org.mimacom.commons.liferay.adapter;


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
