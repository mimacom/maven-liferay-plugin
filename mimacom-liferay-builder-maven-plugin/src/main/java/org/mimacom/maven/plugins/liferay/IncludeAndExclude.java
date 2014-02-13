package org.mimacom.maven.plugins.liferay;


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

/**
 * 
 * @author stni
 * 
 */
public class IncludeAndExclude {
    private String include;

    private String exclude;

    public IncludeAndExclude() {}

    public IncludeAndExclude(String include, String exclude) {
        this.include = include;
        this.exclude = exclude;
    }

    public boolean hasInclude() {
        return getInclude() != null && getInclude().trim().length() > 0;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public String[] getIncludeSplit() {
        return split(getInclude());
    }

    public String[] getExcludeSplit() {
        return split(getExclude());
    }

    public String getIncludeWithSuffixes(String suffix) {
        return addSuffixes(getInclude(), suffix);
    }

    public String getExcludeWithSuffixes(String suffix) {
        return addSuffixes(getExclude(), suffix);
    }

    private static String addSuffixes(String libNames, String suffix) {
        String result = "";
        if (libNames != null) {
            for (String libName : split(libNames)) {
                result += "," + libName + suffix;
            }
            result = result.substring(1);
        }
        return result;
    }

    private static String[] split(String s) {
        String[] res = s.split(",");
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].trim();
        }
        return res;
    }

}
