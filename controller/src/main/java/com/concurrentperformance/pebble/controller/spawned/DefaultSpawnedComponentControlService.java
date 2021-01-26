package com.concurrentperformance.pebble.controller.spawned;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.server.service.RpcServerS2CSupport;
import com.concurrentperformance.pebble.comms.rpc.server.service.SkelitalRpcServerS2CSupport;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorService;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlException;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlServiceC2SApi;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlServiceS2CApi;


public class DefaultSpawnedComponentControlService extends SkelitalRpcServerS2CSupport<SpawnedComponentControlServiceS2CApi>
		implements SpawnedComponentControlServiceC2SApi, 
		RpcServerS2CSupport<SpawnedComponentControlServiceS2CApi> {

	private final Log log = LogFactory.getLog(this.getClass());

	private TopologyMonitorService topologyMonitorService;
	
	@Override
	public void registerSpawnedComponent(long spawnedComponentId, long spawnedComponentInstanceId) throws SpawnedComponentControlException {
		log.info("Registration of [id=" + spawnedComponentId + ", instance=" + spawnedComponentInstanceId + "]");
		topologyMonitorService.componentRegistration(spawnedComponentId, spawnedComponentInstanceId, getConnection());
	}

	@Override
	public void heartbeatSpawnedComponent(long spawnedComponentId, long spawnedComponentInstanceId) throws SpawnedComponentControlException {
		log.trace("Heartbeat from [id=" + spawnedComponentId + ", instance=" + spawnedComponentInstanceId +"]");
		topologyMonitorService.componentHeartbeatTriggered(spawnedComponentId, spawnedComponentInstanceId);
	}
	
	public final void setTopologyMonitorService(TopologyMonitorService topologyMonitorService) {
		this.topologyMonitorService = topologyMonitorService;
	}	
	
}
