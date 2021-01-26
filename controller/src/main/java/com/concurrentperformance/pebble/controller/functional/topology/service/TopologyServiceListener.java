package com.concurrentperformance.pebble.controller.functional.topology.service;

import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;

/**
 * Listener for the internal TopologyService. 
 * This will notify all changes in the structure of the topology. (Machine / Hub / Connection)
 * 
 * @author Stephen Lake
 */
public interface TopologyServiceListener {

	void topologyService_machineCreatedNotification(MachineDescriptor machineDescriptor); 
	void topologyService_machineDeletedNotification(MachineDescriptor machineDescriptor);
	
	void topologyService_hubCreatedNotification(HubDescriptor hubDescriptor); 
	void topologyService_hubSetListenPort(HubDescriptor hubDescriptor);
	void topologyService_hubDeletedNotification(HubDescriptor hubDescriptor);

	void topologyService_containerCreatedNotification(ContainerDescriptor containerDescriptor); 
	void topologyService_containerDeletedNotification(ContainerDescriptor containerDescriptor);
	
	void topologyService_mountCreatedNotification(MountDescriptor mountDescriptor);
	void topologyService_mountDeletedNotification(MountDescriptor mountDescriptor);
	void topologyService_mountHostedNotification(MountDescriptor mountDescriptor);
	void topologyService_mountDehostedNotification(MountDescriptor mountDescriptor);
}
