package com.concurrentperformance.pebble.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class EclipseDeps extends Task {
	
	private String propertyName;
	private String classpathPattern;
	private String moduleName;
	private boolean includeSelf = false;
	private boolean transative = false;
	
	private static final String MODULE_NAME_TOKEN = "MODULE_NAME";
	private static final Pattern PATTERN = 
			Pattern.compile(" path=\"/(.+)\"/>");

    // The method executing the task
    public void execute() throws BuildException {
    	
        Set<String> allDeps = getDependentModules(moduleName);
        
        if (includeSelf) {
        	allDeps.add(moduleName);
        }
        
        String dependencyProperty = buildPropertyString(allDeps);
        setAntProperty(dependencyProperty);
    }

	private Set<String> getDependentModules(String moduleName) {
    	String classpathFileName = buildClasspathFileName(moduleName);
    	log("Find dependency from " + classpathFileName);
    	String contents = readFile(classpathFileName);

    	Set<String> extractedDependentModuleNames = extractMatcherGroup1(contents, PATTERN);
		
		if (transative) {
			Set<String> transiativeDependencies = new HashSet<String>();
			for (String extractedModuleName : extractedDependentModuleNames) {
				transiativeDependencies.addAll(getDependentModules(extractedModuleName));
			}
			
			extractedDependentModuleNames.addAll(transiativeDependencies);
		}
			
		return extractedDependentModuleNames;
	}

	private String buildClasspathFileName(String moduleName) {
		String classpathFileName = classpathPattern.replace(MODULE_NAME_TOKEN, moduleName);
		return classpathFileName;
	}

	private String buildPropertyString(Set<String> allDeps) {
		log("Found dependencys " + allDeps);
        StringBuilder deps = new StringBuilder();
        int i=0;
        for (String dep : allDeps) {
        	deps.append(dep);
        	if (++i < allDeps.size()) {
        		deps.append(",");
        	}
		}
		return deps.toString();
	}

	private String readFile(String path) throws BuildException {
		FileInputStream stream = null;
		try {
			File file = new File(path);
			log("Mine dependencies from eclipse classfile [" + file.getCanonicalPath() + "]");
			stream = new FileInputStream(file);

			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		} catch (Exception e) {
			throw new BuildException(e);
		} finally {
			if (stream  != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
    private void setAntProperty(String value) {
        Project ant= getProject();  
        ant.init();  
        ant.setProperty( propertyName, value);  
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

    public void setClasspathPattern(String classpathPattern) {
        this.classpathPattern = classpathPattern;
    }
    
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public void setProperty(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public void setIncludeSelf(boolean includeSelf) {
        this.includeSelf = includeSelf;
    }

    public void setTransative(boolean transative) {
        this.transative = transative;
    }
}