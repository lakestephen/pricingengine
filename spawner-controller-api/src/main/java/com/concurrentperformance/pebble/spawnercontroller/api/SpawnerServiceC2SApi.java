package com.concurrentperformance.pebble.spawnercontroller.api;


/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public interface SpawnerServiceC2SApi {
	
	void spawnHub(String hubName, long hubId, long hubInstance, 
			String controllersHostName, int controllersHubListeningPort, 
			int hubsContainerListeningPort);
	
	void spawnContainer(String containerName, long containerId, long containerInstance, 
			String controllersHostName, int controllersContainerListeningPort, 
			int hubsContainerListeningPort,
			String databaseURL, String databaseUsername, String databasePassword);

}


