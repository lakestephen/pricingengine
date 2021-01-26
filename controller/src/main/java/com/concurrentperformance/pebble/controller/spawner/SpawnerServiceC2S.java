package com.concurrentperformance.pebble.controller.spawner;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.spawnercontroller.api.SpawnerServiceC2SApi;
import com.concurrentperformance.pebble.spawnercontroller.api.SpawnerServiceS2CApi;

public interface SpawnerServiceC2S extends SpawnerServiceC2SApi,
	RpcIncommingServiceSupport<SpawnerServiceS2CApi> {

}
