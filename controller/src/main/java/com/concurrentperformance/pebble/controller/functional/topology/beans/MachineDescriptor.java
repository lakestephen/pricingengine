package com.concurrentperformance.pebble.controller.functional.topology.beans;

import java.util.Set;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class MachineDescriptor extends TopologyDescriptor {

	private Set<HubDescriptor> hubDescriptors; 

	public TopologyType getType() {
		return TopologyType.MACHINE;
	}

	public Set<HubDescriptor> getHubDescriptors() {
		return hubDescriptors;
	}

	/**
	 * @param hubDescriptors the hubs to set
	 */
	public void setHubDescriptors(Set<HubDescriptor> hubDescriptors) {
		this.hubDescriptors = hubDescriptors;
	}

	public void addHubDescriptor(HubDescriptor hubDescriptor) {
		hubDescriptor.setParentMachine(this);
		hubDescriptors.add(hubDescriptor);
	}	
	
}