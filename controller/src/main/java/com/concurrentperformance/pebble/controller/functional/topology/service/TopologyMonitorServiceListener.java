package com.concurrentperformance.pebble.controller.functional.topology.service;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyDescriptor;


public interface TopologyMonitorServiceListener {
	
	void topologyMonitorService_updateTopologyStatus(TopologyDescriptor topologyDescriptor, TopologyMonitorState topologyState);

	void topologyMonitorService_connectionRegistration(
			TopologyDescriptor topologyDescriptor, RpcConnection connection);

	void topologyMonitorService_connectionDeregistration(TopologyDescriptor topologyDescriptor);
}
