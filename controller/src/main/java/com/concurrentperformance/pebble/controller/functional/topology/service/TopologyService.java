package com.concurrentperformance.pebble.controller.functional.topology.service;

import java.util.Set;

import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.TopologyServiceException;
import com.concurrentperformance.pebble.util.service.ServiceListenerSupport;

public interface TopologyService extends ServiceListenerSupport<TopologyServiceListener> {
//TODO some of these methods could be commoned up if they had a single topology ID sequence 	
	public MachineDescriptor createMachine(String machineName) throws TopologyServiceException;
	public void deleteMachine(long machineId)throws TopologyServiceException;
	public Set<MachineDescriptor> getAllMachines();
	
	public HubDescriptor createHub(long parentMachineId, String hubName) throws TopologyServiceException;
	public void deleteHub(long hubId)throws TopologyServiceException;
	public HubDescriptor getHub(long hubId);
	public Set<HubDescriptor> getAllHubs(MachineDescriptor parentMachine);
	public void setHubListenPort(long hubId, int hubListenPort) throws TopologyServiceException;

	public ContainerDescriptor createContainer(long parentHubId, String containerName) throws TopologyServiceException;
	public void deleteContainer(long containerId) throws TopologyServiceException;
	public ContainerDescriptor getContainer(long containerId);
	public Set<ContainerDescriptor> getAllContainers(HubDescriptor parentHub);
	public Set<ContainerDescriptor> getAllContainers();

	public MountDescriptor createMount(String mountPath) throws TopologyServiceException;
	public void deleteMount(long mountId) throws TopologyServiceException;
	public void hostMountInContainer(long mountId, long hostContainerId) throws TopologyServiceException;
	public void dehostMount(long mountId) throws TopologyServiceException;
	public MountDescriptor getMount(long mountId);
	public Set<MountDescriptor> getAllMounts(ContainerDescriptor container);
	public Set<MountDescriptor> getAllMounts();

	public String getTopologyName(long topologyId);
}