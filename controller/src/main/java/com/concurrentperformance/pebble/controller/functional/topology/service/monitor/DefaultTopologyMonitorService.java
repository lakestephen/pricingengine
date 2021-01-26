package com.concurrentperformance.pebble.controller.functional.topology.service.monitor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.SpawanableComponentDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.dao.TopologyDao;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMaintainenceService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorServiceListener;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorState;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyServiceListener;
import com.concurrentperformance.pebble.controllerspawned.api.SpawnedComponentControlException;
import com.concurrentperformance.pebble.util.service.AsynchServiceListenerSupport;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */

//TODO to properly manage the topology, we need to give each created topology node and instance id that will ensure that any rogue processes are shut down. 
//TODO Need to listen for disconnects of the Hub/Container to speed the regeneration process. 
public class DefaultTopologyMonitorService extends AsynchServiceListenerSupport<TopologyMonitorServiceListener> 
	implements TopologyMonitorService, TopologyServiceListener  {
	
	private final Log log = LogFactory.getLog(this.getClass());

	private static long MONITOR_INTERVAL_MS = 100;
	
	private TopologyDao topologyDao;
	private TopologyMaintainenceService topologyMaintainenceService;

	private final ConcurrentMap<Long, MonitorableComponentStatus> monitorableComponentStatuses = new ConcurrentHashMap<Long, MonitorableComponentStatus>();
	
	public void start() {  
		bootstrap();
		startMonitor();
	}

	private void bootstrap() {
		Set<MachineDescriptor> machines = topologyDao.getAllMachine();
		for (MachineDescriptor machineDescriptor : machines) {
			topologyService_machineCreatedNotification(machineDescriptor);
			Set<HubDescriptor> hubs = machineDescriptor.getHubDescriptors();
			for (HubDescriptor hubDescriptor : hubs) {
				bootstrapHub(hubDescriptor);
			}		
		}
	}

	private void bootstrapHub(HubDescriptor hubDescriptor) {

		topologyService_hubCreatedNotification(hubDescriptor);

		Set<ContainerDescriptor> containers = hubDescriptor.getContainerDescriptors();
		for (ContainerDescriptor containerDescriptor : containers) {
			topologyService_containerCreatedNotification(containerDescriptor);
		}
		
	}
	
	private void startMonitor() {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				while (true) {

					long startTime = System.currentTimeMillis();
					
					for (MonitorableComponentStatus status : monitorableComponentStatuses.values()) {
						
						status.monitor();
						
						if (status.shouldRemove()) {
							log.info("Removing from topology monitor [" + status + "]");
							monitorableComponentStatuses.remove(status.getId());
						}
					}					

					sleepUntilNextRun(startTime);
				}
			}

			private void sleepUntilNextRun(long startTime) {
				// We try to trigger the monitoring at regular intervals, 
				// rather than a static delay. 
				long sleepTimeout = (startTime + MONITOR_INTERVAL_MS) - System.currentTimeMillis();
				
				//pause
				try {
					if (sleepTimeout > 0) {
						TimeUnit.MILLISECONDS.sleep(sleepTimeout);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					log.error("TODO ", e);
				}
			}
		};
		
		new Thread(r, "TopologyMonitor").start(); //TODO either make the run task more robust, or use an Executor
	}

	
	@Override  //TODO We should register and trigger this directly into the Status item to save the lookup. 
	public void componentRegistration(long spawnedComponentId, long spawnedComponentInstanceId, RpcConnection connection) throws SpawnedComponentControlException {
		MonitorableComponentStatus monitorableComponentStatus = monitorableComponentStatuses.get(spawnedComponentId);
		if (monitorableComponentStatus != null) {
			monitorableComponentStatus.componentRegistration(connection, spawnedComponentInstanceId);
		}
		else {
			String msg = "Registration from unknown [" + spawnedComponentId +"], please shut yourself down";
			log.error(msg);
			throw new SpawnedComponentControlException (msg);
		}
	}

	@Override  //TODO We should register and trigger this directly into the Status item to save the lookup. 
	public void componentHeartbeatTriggered(long spawnedComponentId, long spawnedComponentInstanceId) throws SpawnedComponentControlException {
		MonitorableComponentStatus monitorableComponentStatus = monitorableComponentStatuses.get(spawnedComponentId);
		if (monitorableComponentStatus != null) {
			monitorableComponentStatus.triggerHeartbeat(spawnedComponentInstanceId);
		}
		else {
			String msg = "Heartbeat from unknown [" + spawnedComponentId +"], please shut yourself down";
			log.error(msg);
			throw new SpawnedComponentControlException (msg);
		}
	}

	@Override
	public void topologyService_machineCreatedNotification(MachineDescriptor machineDescriptor) {
		log.info("Monitoring new machine [" + machineDescriptor + "]" );
	
		MachineStatus status = new MachineStatus(machineDescriptor, this, topologyMaintainenceService);
		//TODO use putIfAbsent and handle the duplicate. 
		long id = status.getId();
		if (id <= 0 ){
			throw new IllegalStateException("Cant monitor spawned component of invalid id [" + id + "]");
		}
		monitorableComponentStatuses.put(id, status);
	}

	@Override
	public void topologyService_machineDeletedNotification(MachineDescriptor machineDescriptor) {
		componentDeletedNotification(machineDescriptor);
	}

	@Override
	public void topologyService_hubCreatedNotification(HubDescriptor hubDescriptor) {
		log.info("Monitoring new hub [" + hubDescriptor + "]" );
		spawnedComponentCreatedNotification(hubDescriptor);
	}

	@Override
	public void topologyService_hubSetListenPort(HubDescriptor hubDescriptor) {
		//first shut down all the containers that are attached to the hub.
		for(ContainerDescriptor containerDescriptor : hubDescriptor.getContainerDescriptors()) {
			componentDeletedNotification(containerDescriptor);
			waitForSpawnedComponentToDie(containerDescriptor);
		}
		
		//Then take the Hub down. 
		componentDeletedNotification(hubDescriptor);
		waitForSpawnedComponentToDie(hubDescriptor);
		
		// finally rebuild
		bootstrapHub(hubDescriptor);
	}

	@Override
	public void topologyService_hubDeletedNotification(HubDescriptor hubDescriptor) {
		componentDeletedNotification(hubDescriptor);
	}

	@Override
	public void topologyService_containerCreatedNotification(ContainerDescriptor containerDescriptor) {
		log.info("Monitoring new container [" + containerDescriptor + "]" );
		spawnedComponentCreatedNotification(containerDescriptor);
	}

	@Override
	public void topologyService_containerDeletedNotification(ContainerDescriptor containerDescriptor) {
		componentDeletedNotification(containerDescriptor);
	}
	
	private void spawnedComponentCreatedNotification(SpawanableComponentDescriptor spawanableComponentDescriptor) {
		SpawnedComponentStatus status = new SpawnedComponentStatus(spawanableComponentDescriptor, this, topologyMaintainenceService);
		//TODO use putIfAbsent and handle the duplicate. 
		long id = status.getId();
		if (id <= 0 ){
			throw new IllegalStateException("Cant monitor spawned component of invalid id [" + id + "], [" + spawanableComponentDescriptor + "]");
		}
		monitorableComponentStatuses.put(id, status);
	}
	
	private void componentDeletedNotification(TopologyDescriptor topologyDescriptor) {
		MonitorableComponentStatus monitorableComponentStatus = monitorableComponentStatuses.get(topologyDescriptor.getId());
		if (monitorableComponentStatus != null) {
			monitorableComponentStatus.componentDeletedNotification();
		}
	}
	
	private void waitForSpawnedComponentToDie(SpawanableComponentDescriptor spawanableComponentDescriptor) {
		log.info("Waiting for [" + spawanableComponentDescriptor + "] to die");
		while (monitorableComponentStatuses.get(spawanableComponentDescriptor.getId()) != null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// SJL Auto-generated catch block
				log.error("SJL ", e);
			}
		}	
		log.info("[" + spawanableComponentDescriptor + "] has died!");
	}

	public final void setTopologyService(TopologyService topologyService) {
		topologyService.register(this);
	}

	public final void setTopologyMaintainenceService(TopologyMaintainenceService topologyMaintainenceService) {
		this.topologyMaintainenceService = topologyMaintainenceService;
	}

	public void setTopologyDao(TopologyDao topologyDao) {
		this.topologyDao = topologyDao;
	}

	@Override
	public TopologyMonitorState getTopologyState(long topologyId) {
		TopologyMonitorState topologyState = null;
		MonitorableComponentStatus status = monitorableComponentStatuses.get(topologyId);
		if (status != null) {
			topologyState = status.getTopologyState();
		}
		return topologyState;
	}

	@Override
	public void topologyService_mountCreatedNotification(MountDescriptor mountDescriptor) {
		// Do Nothing
	}

	@Override
	public void topologyService_mountDeletedNotification(MountDescriptor mountDescriptor) {
		// Do Nothing
	}

	@Override
	public void topologyService_mountHostedNotification(MountDescriptor mountDescriptor) {
		// SJL Auto-generated method stub
	}
	
	@Override
	public void topologyService_mountDehostedNotification(MountDescriptor mountDescriptor) {
		// SJL Auto-generated method stub
		
	}
}
