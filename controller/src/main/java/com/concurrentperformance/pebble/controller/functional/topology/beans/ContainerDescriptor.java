package com.concurrentperformance.pebble.controller.functional.topology.beans;

import java.util.Set;


/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class ContainerDescriptor extends SpawanableComponentDescriptor {

	private HubDescriptor parentHub;
	private Set<MountDescriptor> hostedMounts; 

	@Override
	public TopologyType getType() {
		return TopologyType.CONTAINER;
	}
		
	public HubDescriptor getParentHub() {
		return parentHub;
	}
	
	public void setParentHub(HubDescriptor parentHub) { 
		this.parentHub = parentHub;
	}
	
	public Set<MountDescriptor> getHostedMounts() {
		return hostedMounts;
	}

	protected void setHostedMounts(Set<MountDescriptor> hostedMounts) {
		this.hostedMounts = hostedMounts;
	}

	public void addHostedMount(MountDescriptor hostedMount) {
		hostedMount.setHostContainer(this);
		hostedMounts.add(hostedMount);
	}

	public void removeHostedMount(MountDescriptor hostedMount) {
		hostedMount.setHostContainer(null);
		hostedMounts.remove(hostedMount);
	}
}