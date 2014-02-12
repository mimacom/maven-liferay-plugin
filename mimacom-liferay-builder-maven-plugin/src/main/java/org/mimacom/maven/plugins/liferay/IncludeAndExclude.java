package org.mimacom.maven.plugins.liferay;


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
