package com.concurrentperformance.pebble.spawnedcommon.controller;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlServiceC2SApi;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlServiceS2CApi;

public interface SpawnedComponentControlServiceC2S extends SpawnedComponentControlServiceC2SApi,
	RpcIncommingServiceSupport<SpawnedComponentControlServiceS2CApi> {

}
