package com.concurrentperformance.pebble.controller.functional.topology.dao;

import java.util.Set;

import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyDescriptor;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public interface TopologyDao {

	void persistTopology(TopologyDescriptor topologyDescriptor);
	void deleteTopology(TopologyDescriptor existingTopologyDescriptor);
	TopologyDescriptor findTopologyById(long topologyId);

	Set<MachineDescriptor> getAllMachine();
	MachineDescriptor findMachineById(long machineId);

	Set<HubDescriptor> getAllHubs(MachineDescriptor parentMachineDescriptor);
	HubDescriptor findHubById(long hubId);

	Set<ContainerDescriptor> getAllContainers(HubDescriptor parentHubDescriptor);
	Set<ContainerDescriptor> getAllContainers();
	ContainerDescriptor findContainerById(long containerId);
	void deleteContainer(ContainerDescriptor existingContainerDescriptor);
	
	MountDescriptor findMountById(long mountId);
	Set<MountDescriptor> getAllMountItems();
	Set<MountDescriptor> getAllMountItems(ContainerDescriptor hostContainer);
}
