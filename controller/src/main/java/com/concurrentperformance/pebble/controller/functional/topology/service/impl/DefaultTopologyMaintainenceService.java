package com.concurrentperformance.pebble.controller.functional.topology.service.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.client.RpcClientConnectionFactory;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.SpawanableComponentDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyType;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMaintainenceService;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.SpawnerServiceUnavailable;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.TopologyMaintainanceException;
import com.concurrentperformance.pebble.controller.spawner.SpawnerServiceC2S;

public class DefaultTopologyMaintainenceService implements TopologyMaintainenceService { 

	private final Log log = LogFactory.getLog(this.getClass());
		
	private int controllersHubListeningPort;	
	private int controllersContainerListeningPort;	
	private int spawnersControllerListeningPort;	
	
	private String databaseURL;
	private String databaseUsername;
	private String databasePassword;
	
	private RpcClientConnectionFactory spawnerConnectionFactory;
	
	private Map<MachineDescriptor, SpawnerServiceC2S> spawnerServices = 
			new ConcurrentHashMap<MachineDescriptor, SpawnerServiceC2S>();
	
	@Override
	public void createMachine(MachineDescriptor machineDescriptor) throws TopologyMaintainanceException {
		log.info("Creating new machine for [" + machineDescriptor + "]");
		// ping the machine
		try { 
			String name = machineDescriptor.getName();
			InetAddress address = InetAddress.getByName(name);
			boolean reachable = address.isReachable(2000);

			log.info("Address [" + address + "], status [" + (reachable? "Reachable" : "Not Reachable") + "]");
			if (!reachable) {
				throw new TopologyMaintainanceException("Can't create machine instance [" + name + "] is not reachable");
			}
			
			// create a new connection to the spawner
			RpcConnection connection = (RpcConnection)spawnerConnectionFactory.createAndStartNewConnection(name, spawnersControllerListeningPort);
			
			SpawnerServiceC2S spawnerService = connection.getService(SpawnerServiceC2S.class);
			if (spawnerService == null) {
				throw new IllegalStateException("No SpawnerServiceC2S defined in connection");
			}
			spawnerServices.put(machineDescriptor, spawnerService);
			
			log.info("Created spawner service for [" + machineDescriptor + "]");
			
		} catch (IOException e) {
			throw new TopologyMaintainanceException(e);
		}
	}
	
	@Override
	public void createSpawanableComponent(SpawanableComponentDescriptor spawanableComponentDescriptor, long instanceId) throws SpawnerServiceUnavailable  {
		// get the spawner service from its parent machine details.

		TopologyType type = spawanableComponentDescriptor.getType();
		log.info("Creating new [" + type + "] for [" + spawanableComponentDescriptor + "]");
		switch (type) {
			case HUB:
				createHub((HubDescriptor)spawanableComponentDescriptor, instanceId);
				break;
			case CONTAINER:
				createContainer((ContainerDescriptor)spawanableComponentDescriptor, instanceId);
				break;
			default:
				log.error("Cant create new [" + type + " ] for [" + spawanableComponentDescriptor + "] - unhandled");
		}
	}

	private void createHub(HubDescriptor hubDescriptor, long instanceId) throws SpawnerServiceUnavailable  {
		MachineDescriptor machineDescriptor = hubDescriptor.getParentMachine();
		SpawnerServiceC2S spawnerService = getSpawnerService(machineDescriptor);
		if (spawnerService == null) {
			throw new SpawnerServiceUnavailable("No spawner service yet for [" + machineDescriptor + "], for hub [" + hubDescriptor + "]");
		}

		String hubName = hubDescriptor.getName();
		long hubId = hubDescriptor.getId();
		int hubListenerPort = hubDescriptor.getListenPort();

		spawnerService.spawnHub(hubName, hubId, instanceId, 
				getHostName(), controllersHubListeningPort, 
				hubListenerPort);
	}
	
	private void createContainer(ContainerDescriptor containerDescriptor, long instanceId) throws SpawnerServiceUnavailable {
		MachineDescriptor machineDescriptor = containerDescriptor.getParentHub().getParentMachine();
		SpawnerServiceC2S spawnerService = getSpawnerService(machineDescriptor);
		if (spawnerService == null) {
			throw new SpawnerServiceUnavailable("No spawner service yet for [" + machineDescriptor + "], for container [" + containerDescriptor + "]");
		}
		
		String containerName = containerDescriptor.getName();
		long containerId = containerDescriptor.getId();
		int parentHubListenerPort = containerDescriptor.getParentHub().getListenPort();

		spawnerService.spawnContainer(containerName, containerId, instanceId,
				getHostName(), controllersContainerListeningPort, 
				parentHubListenerPort, 
				databaseURL, databaseUsername, databasePassword);
	}

	private SpawnerServiceC2S getSpawnerService(MachineDescriptor machineDescriptor) {
		SpawnerServiceC2S spawnerService = spawnerServices.get(machineDescriptor);
		return spawnerService;
	}

	private String getHostName() {
		//TODO mine proper host name. 
		return "127.0.0.1";
	}

	public void setControllersHubListeningPort(int controllersHubListeningPort) {
		this.controllersHubListeningPort = controllersHubListeningPort;
	}
	
	public void setControllersContainerListeningPort(int controllersContainerListeningPort) {
		this.controllersContainerListeningPort = controllersContainerListeningPort;
	}
	
	public void setSpawnersControllerListeningPort(int spawnersControllerListeningPort) {
		this.spawnersControllerListeningPort = spawnersControllerListeningPort;
	}
	
	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public void setSpawnerConnectionFactory(RpcClientConnectionFactory spawnerConnectionFactory) {
		this.spawnerConnectionFactory = spawnerConnectionFactory;
	}	
}
