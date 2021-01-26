package com.concurrentperformance.pebble.controller.functional.topology.beans;


/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class MountDescriptor extends TopologyDescriptor {

	private ContainerDescriptor hostContainer;

	public TopologyType getType() {
		return TopologyType.MOUNT;
	}

	public ContainerDescriptor getHostContainer() {
		return hostContainer;
	}
	
	public void setHostContainer(ContainerDescriptor hostContainer) { 
		this.hostContainer = hostContainer;
	}
	
	public String getPath() {
		return getName();
	}

}