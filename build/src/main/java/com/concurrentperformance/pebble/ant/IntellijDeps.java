package com.concurrentperformance.pebble.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntellijDeps extends Task {

    public static final String TAB_STEP = " | ";

    private String moduleDependencyParam;
    private String jarDependencyParam;
    private File projectRootDir;
    private String moduleName;

	private static final String MODULE_NAME_TOKEN = "MODULE_NAME";

    private static final Pattern DEP_JARS_PATTERN =
            Pattern.compile("<root url=\"jar://\\$MODULE_DIR\\$(.+)\\!/\" />");
    private static final Pattern DEP_MODULES_PATTERN =
            Pattern.compile("<orderEntry type=\"module\" module-name=\"(.+)\" />");

    // The method executing the task
        public void execute() throws BuildException {
            try {
                List<String> allDependentModuleNames = getAllDependentModuleNames();
                Set<String> allDependentJars = getAllDependentJarPaths(allDependentModuleNames);

                if (moduleDependencyParam != null) {
                    setAntProperty(moduleDependencyParam, allDependentModuleNames);
                }
                if (jarDependencyParam != null) {
                    setAntProperty(jarDependencyParam, allDependentJars);
                }

            } catch (IOException e) {
                throw new BuildException(e);
            }
        }

    private List<String> getAllDependentModuleNames() throws IOException {
        log("", Project.MSG_DEBUG);
        log("Finding Module dependencies for [" + moduleName + "]", Project.MSG_DEBUG);
        List<String> allDependentModuleNames = new ArrayList<>();
        allDependentModuleNames.add(moduleName);
        getDependentModulesRecursively(moduleName, allDependentModuleNames, "");
        log("Found transative module dependencies for [" + moduleName + "] -> " + allDependentModuleNames);
        return allDependentModuleNames;
    }

    private void getDependentModulesRecursively(String moduleName, List<String> allDependentModuleNames, String logTabDepth) throws IOException {
        log(logTabDepth +  "Examine module [" + moduleName + "]", Project.MSG_DEBUG);

        String contents = readModuleProjectFile(moduleName, logTabDepth);
        Set<String> extractedDependentModuleNames = extractMatcherGroup1(contents, DEP_MODULES_PATTERN);
        log(logTabDepth + " Depends on " + extractedDependentModuleNames, Project.MSG_DEBUG);
        for (String extractedDependentModuleName : extractedDependentModuleNames) {
            // If we don't already have this module in the set, then recursively get its children
            if (!allDependentModuleNames.contains(extractedDependentModuleName)) {
                allDependentModuleNames.add(extractedDependentModuleName);
                getDependentModulesRecursively(extractedDependentModuleName, allDependentModuleNames, logTabDepth + TAB_STEP);
            }
            else {
                log(logTabDepth + " module already included [" + extractedDependentModuleName + "]", Project.MSG_DEBUG);
            }
        }
    }

    private Set<String> getAllDependentJarPaths(List<String> allDependentModuleNames) throws IOException {
        log("", Project.MSG_DEBUG);
        log("Finding Jar dependencies for [" + moduleName + "], using modules [" + allDependentModuleNames + "]", Project.MSG_DEBUG);
        Set<String> allDependentJars = new HashSet<>();
        for (String dependentModuleName : allDependentModuleNames) {
            Set<String> dependentModuleJars = getDependentJars(dependentModuleName);
            allDependentJars.addAll(dependentModuleJars);
        }
        log("Found jar dependencies for module [" + moduleName + "] -> [" + allDependentJars);
        return allDependentJars;
    }

    private Set<String> getDependentJars(String moduleName) throws IOException {
        log("Module [" + moduleName + "]", Project.MSG_DEBUG);
        String contents = readModuleProjectFile(moduleName, "");
        Set<String> extractedDependentModuleNames = extractMatcherGroup1(contents, DEP_JARS_PATTERN);
        Set<String> absoluteJarPaths = new HashSet<>();
        String pathPrefix = buildModuleRootDir(moduleName, "");
        for (String extractedDependentModuleName : extractedDependentModuleNames) {
            try {
                File file = new File(pathPrefix + extractedDependentModuleName);
                String canonicalPath = file.getCanonicalPath();
                absoluteJarPaths.add(canonicalPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log(" Depends on " + absoluteJarPaths , Project.MSG_DEBUG);
        return absoluteJarPaths;
    }

    private String readModuleProjectFile(String moduleName, String logTabDepth) throws IOException {
        String classpathFileName = buildModuleProjectFileName(moduleName, logTabDepth);
        String contents = readFile(classpathFileName, logTabDepth);
        return contents;
    }

    private String buildModuleProjectFileName(String moduleName, String logTabDepth) throws IOException {
        String moduleProjectFileName = buildModuleRootDir(moduleName, logTabDepth) + "\\" +
                moduleName + ".iml";
        log(logTabDepth + " Build project file name. projectRootDir [" + projectRootDir.getCanonicalPath() + "], " +
                "Module Name[" + moduleName + "], " +
                "Result[" + moduleProjectFileName + "]",
                Project.MSG_DEBUG);

        return moduleProjectFileName;
    }


    private String buildModuleRootDir(String moduleName, String logTabDepth) throws IOException {
        String moduleRootDir = projectRootDir.getCanonicalPath()  + "\\" +
                moduleName + "\\" ;
        log(logTabDepth + " Build module root path. projectRootDir [" + projectRootDir.getCanonicalPath() + "], " +
                "Module Name[" + moduleName + "], " +
                "Result[" + moduleRootDir + "]",
                Project.MSG_DEBUG);

        return moduleRootDir;
    }

    private String readFile(String path, String logTabDepth) throws BuildException {
        FileInputStream stream = null;
        try {
            File file = new File(path);
            log(logTabDepth + " Read Intellij project file [" + file.getCanonicalPath() + "]", Project.MSG_DEBUG);
            stream = new FileInputStream(file);

            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        } catch (Exception e) {
            throw new BuildException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private String buildPropertyValue(Collection<String> property) {
        StringBuilder propValue = new StringBuilder();
        int i=0;
        for (String dep : property) {
            propValue.append(dep);
            if (++i < property.size()) {
                propValue.append(",");
            }
        }
        return propValue.toString();
    }

    private void setAntProperty(String name, Collection<String> valueSet) {
        String value = buildPropertyValue(valueSet);

        Project ant = getProject();
        if (ant == null) {
            log("Ant not available to set: name[" + name + "], valueSet" + valueSet);
        }
        else {
            ant.init();
            ant.setProperty(name, value);
        }
	}

	/**
	 * Generic matcher method that extracts group 1
	 */
    protected Set<String> extractMatcherGroup1(String input, Pattern pattern) {
		Set<String> group = new HashSet<String>();
        Matcher matcher = pattern.matcher(input);
        while ( matcher.find() ) {
        	group.add(matcher.group(1));
        }
    	return group;
	}

    public void setJarDependencyParam(String jarDependencyParam) {
        this.jarDependencyParam = jarDependencyParam;
    }

    public void setModuleDependencyParam(String moduleDependencyParam) {
        this.moduleDependencyParam = moduleDependencyParam;
    }

    public void setProjectRootDir(File projectRootDir) {
        this.projectRootDir = projectRootDir;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}