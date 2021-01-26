package com.concurrentperformance.pebble.spawner.controller.impl;

import java.util.List;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class JvmSpawnerDescriptor {
	
	private String canonicalClassName;
	private String classPath;
	private List<String> vmArgs;
	private String directory;
	
	
	public String getCanonicalClassName() {
		return canonicalClassName;
	}
	public void setCanonicalClassName(String canonicalClassName) {
		this.canonicalClassName = canonicalClassName;
	}
	
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	
	public List<String> getVmArgs() {
		return vmArgs;
	}
	public void setVmArgs(List<String> vmArgs) {
		this.vmArgs = vmArgs;
	}
	
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	@Override
	public String toString() {
		return "JvmSpawnerDescriptor [canonicalClassName=" + canonicalClassName
				+ ", classPath=" + classPath + ", vmArgs=" + vmArgs
				+ ", directory=" + directory + "]";
	}
}