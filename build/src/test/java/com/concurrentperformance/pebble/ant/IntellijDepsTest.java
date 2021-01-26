package com.concurrentperformance.pebble.ant;

import org.junit.Test;

import java.io.File;

/**
 * TODO
 *
 * @author: Stephen
 */
public class IntellijDepsTest {

    @Test
    public void runDeps() {
        com.concurrentperformance.pebble.ant.IntellijDeps deps = new com.concurrentperformance.pebble.ant.IntellijDeps();
        deps.setProjectRootDir(new File("."));
        deps.setModuleName("controller");
        deps.setModuleDependencyParam("module");
        deps.setJarDependencyParam("jar");
        deps.execute();
    }
}
