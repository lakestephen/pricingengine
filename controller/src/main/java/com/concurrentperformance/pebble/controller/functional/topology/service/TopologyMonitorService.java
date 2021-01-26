package com.concurrentperformance.pebble.controller.functional.topology.service;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlException;
import com.concurrentperformance.pebble.util.service.ServiceListenerSupport;

/**
 * Service that understands the topology that should be in force, monitors 
 * the state of the actual topology and recreates missing nodes, reporting 
 * state to its listeners. 
 *
 * @author Stephen Lake
 */
public interface TopologyMonitorService extends ServiceListenerSupport<TopologyMonitorServiceListener> {

	void componentRegistration(long spawnedComponentId, long spawnedComponentInstanceId, RpcConnection connection) throws SpawnedComponentControlException;

	void componentHeartbeatTriggered(long spawnedComponentId, long spawnedComponentInstanceId) throws SpawnedComponentControlException;

	TopologyMonitorState getTopologyState(long topologyId);
}
