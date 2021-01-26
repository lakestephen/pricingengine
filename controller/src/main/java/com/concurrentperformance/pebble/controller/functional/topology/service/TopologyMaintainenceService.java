package com.concurrentperformance.pebble.controller.functional.topology.service;

import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.SpawanableComponentDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.SpawnerServiceUnavailable;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.TopologyMaintainanceException;

/**
 * This is an internal service that encapsulates the process of creating real
 * instances of the topology (as opposed to the persistent meta data of the topology)
 * @author Stephen
 *
 */
public interface TopologyMaintainenceService {

	void createMachine(MachineDescriptor machineDescriptor) throws TopologyMaintainanceException;

	void createSpawanableComponent(SpawanableComponentDescriptor spawanableComponent, long instanceId) throws SpawnerServiceUnavailable ;

}
