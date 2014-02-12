package org.mimacom.maven.plugins.liferay;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class Lister {
    private static class Filter {
        private final String filter;

        public Filter(String filter) {
            this.filter = filter.replace('.', '/');
        }

        @Override
        public String toString() {
            return filter;
        }

        public boolean matches(String s) {
            return s.startsWith(filter);
        }

        public static boolean matchesAny(String s, List<Filter> filters) {
            for (Filter filter : filters) {
                if (filter.matches(s)) {
                    return true;
                }
            }
            return false;
        }
    }

    public class Result {
        private Map<String, List<String>> files = new TreeMap<String, List<String>>();

        void checkFile(String fileName, String origin) {
            if (!includes.isEmpty() && !Filter.matchesAny(fileName, includes)) {
                return;
            }
            if (!excludes.isEmpty() && Filter.matchesAny(fileName, excludes)) {
                return;
            }
            List<String> foundIn = files.get(fileName);
            if (foundIn == null) {
                foundIn = new ArrayList<String>(1);
                files.put(fileName, foundIn);
            }
            foundIn.add(origin);
        }

        public Map<String, List<String>> getFiles() {
            return files;
        }
    }

    private Result result = new Result();

    private List<Filter> includes = new ArrayList<Filter>();

    private List<Filter> excludes = new ArrayList<Filter>();

    public Result getResult() {
        return result;
    }

    public void addInclude(String include) {
        includes.add(new Filter(include));
    }

    public void addExclude(String exclude) {
        excludes.add(new Filter(exclude));
    }

    public void analyzeZipWithJarClasses(File zipFile) throws IOException {
        ZipFile zf = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = zf.entries();
        byte[] buf = new byte[10000];
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                String name = entry.getName();
                if (name.endsWith(".jar")) {
                    File outFile = File.createTempFile("patchanalyze", "jar");
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
                    InputStream in = zf.getInputStream(entry);
                    int read;
                    while ((read = in.read(buf)) > 0) {
                        out.write(buf, 0, read);
                    }
                    in.close();
                    out.close();
                    int pos = name.lastIndexOf('/');
                    analyzeJarClasses(name.substring(pos + 1, name.length()) + " in " + zipFile.getName(), outFile);
                }
            }
        }
    }

    public void analyzeJarFiles(String baseDir,File jarFile) throws IOException {
        ZipFile zf = new ZipFile(jarFile);
        Enumeration<? extends ZipEntry> entries = zf.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                String name = entry.getName();
                int pos = name.indexOf(baseDir + "/");
                if (pos >= 0) {
                    result.checkFile(name.substring(pos + baseDir.length() + 1), jarFile.getName());
                }
            }
        }
    }

    public void analyzeFiles(File baseDir, String baseName)  {
        int baseLen = baseDir.getAbsolutePath().length();
        File[] files = baseDir.listFiles();
        for (File file : files) {
            result.checkFile(file.getAbsolutePath().substring(baseLen), "files in " + baseName);
        }
    }

    public void analyzeWarClasses(File warFile) throws IOException {
        ZipFile zf = new ZipFile(warFile);
        Enumeration<? extends ZipEntry> entries = zf.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                String name = entry.getName();
                if (name.startsWith("WEB-INF/classes/") && name.endsWith(".class")) {
                    String cn = name.substring(16, name.length() - 6);
                    result.checkFile(cn, "files in " + warFile.getName());
                }
                if (name.startsWith("WEB-INF/lib/") && name.endsWith(".jar")) {
                    File outFile = createTempFile(zf.getInputStream(entry));
                    int pos = name.lastIndexOf('/');
                    analyzeJarClasses(name.substring(pos + 1, name.length()) + " in " + warFile.getName(), outFile);
                }
            }
        }
    }

    private File createTempFile(InputStream in) throws IOException {
        byte[] buf = new byte[10000];
        File outFile = File.createTempFile("patchanalyze", "jar");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
        int read;
        while ((read = in.read(buf)) > 0) {
            out.write(buf, 0, read);
        }
        in.close();
        out.close();
        return outFile;
    }

    public void analyzeJarClasses(String jarName, File jarFile) throws IOException {
        ZipFile zf = new ZipFile(jarFile);
        Enumeration<? extends ZipEntry> entries = zf.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    result.checkFile(name.substring(0, name.length() - 6), jarName);
                }
            }
        }
    }

    public void analyzeList(List<String> classes) {
        for (String clazz : classes) {
            result.checkFile(clazz, "list");
        }

    }
}
