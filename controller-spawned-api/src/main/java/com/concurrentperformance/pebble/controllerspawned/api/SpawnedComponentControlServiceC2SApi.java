package com.concurrentperformance.pebble.controllerspawned.api;


public interface SpawnedComponentControlServiceC2SApi {  

	void registerSpawnedComponent(long spawnedComponentId, long spawnedComponentInstanceId) throws SpawnedComponentControlException;
	void heartbeatSpawnedComponent(long spawnedComponentId, long spawnedComponentInstanceId) throws SpawnedComponentControlException;
	
}
