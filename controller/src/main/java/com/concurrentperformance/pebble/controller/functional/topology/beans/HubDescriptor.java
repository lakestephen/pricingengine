package com.concurrentperformance.pebble.controller.functional.topology.beans;

import java.util.Set;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class HubDescriptor extends SpawanableComponentDescriptor {

	private MachineDescriptor parentMachine;
	private Set<ContainerDescriptor> containerDescriptors; 
	private int listenPort;
	
	public TopologyType getType() {
		return TopologyType.HUB;
	}

	public MachineDescriptor getParentMachine() {
		return parentMachine;
	}
	
	public void setParentMachine(MachineDescriptor parentMachine) { 
		this.parentMachine = parentMachine;
	}
	
	public Set<ContainerDescriptor> getContainerDescriptors() {
		return containerDescriptors;
	}

	protected void setContainerDescriptors(Set<ContainerDescriptor> containerDescriptors) {
		this.containerDescriptors = containerDescriptors;
	}

	public void addContainerDescriptor(ContainerDescriptor containerDescriptor) {
		containerDescriptor.setParentHub(this);
		containerDescriptors.add(containerDescriptor);
	}

	public void removeContainerDescriptor(ContainerDescriptor containerDescriptor) {
		containerDescriptors.remove(containerDescriptor);
		// get constraint violations if this is set to null.
//		containerDescriptor.setParentHub(null);		
	}

	public int getListenPort() {
		return listenPort;
	}
	
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;		
	}
	
}