package com.concurrentperformance.pebble.controller.functional.topology.service.monitor;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorState;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlException;

public interface MonitorableComponentStatus {

	Long getId();
	TopologyMonitorState getTopologyState();

	void monitor();

	boolean shouldRemove();

	void componentRegistration(RpcConnection connection, long instanceId) throws SpawnedComponentControlException;
	void triggerHeartbeat(long instanceId) throws SpawnedComponentControlException;
	void componentDeletedNotification();
}
