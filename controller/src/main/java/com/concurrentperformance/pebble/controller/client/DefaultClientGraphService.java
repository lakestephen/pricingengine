package com.concurrentperformance.pebble.controller.client;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.comms.rpc.server.service.RpcServerS2CSupport;
import com.concurrentperformance.pebble.comms.rpc.server.service.SkelitalRpcServerS2CSupport;
import com.concurrentperformance.pebble.controller.container.ContainerGraphService;
import com.concurrentperformance.pebble.controller.container.ContainerGraphServiceListener;
import com.concurrentperformance.pebble.controller.container.ContainerMonitorEventService;
import com.concurrentperformance.pebble.controller.container.ContainerMonitorEventServiceListener;
import com.concurrentperformance.pebble.controller.functional.container.exception.ContainerConnectionException;
import com.concurrentperformance.pebble.controller.functional.container.service.ContainerConnectionService;
import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyServiceListener;
import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.controllerclient.api.graph.ClientGraphServiceC2SApi;
import com.concurrentperformance.pebble.controllerclient.api.graph.ClientGraphServiceS2CApi;
import com.concurrentperformance.pebble.controllercontainer.api.graph.ContainerGraphServiceS2CApi;
import com.concurrentperformance.pebble.controllercontainer.api.graph.exception.ContainerGraphServiceException;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.IntEvent;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculation;

/**
 * TODO 
 * 
 * @author Stephen Lake
 *
 */

//TODO we *could* register the proxy directly with the service. 
//TODO We COULD have a query scripted language.
public class DefaultClientGraphService extends SkelitalRpcServerS2CSupport<ClientGraphServiceS2CApi>
		implements 	ClientGraphServiceC2SApi, 
					RpcServerS2CSupport<ClientGraphServiceS2CApi>, 
					ConnectionListener, 
					TopologyServiceListener, 
					ContainerGraphServiceListener, 
					ContainerMonitorEventServiceListener {

	private final Log log = LogFactory.getLog(this.getClass());

	private TopologyService topologyService;
	private ContainerConnectionService containerConnectionService; 
	private ContainerGraphService containerGraphService; //TODO why cant we use this as service and listener?
	private ContainerMonitorEventService containerMonitorEventService; //TODO why cant we use this as service and listener?

	@Override
	public long addGraphItem(long mountId, String graphPath, Class<? extends GraphCalculation> graphItem, List<String> inputEventIds, String outputEventId) throws ClientException {
		log.info("Add Graph Item ["  + mountId + ", " + graphPath + "]");
		
		// check we have a container mapping for the specified mount 
		MountDescriptor mount = topologyService.getMount(mountId); //TODO for speed, this should be a check of an in memory cache 
		if (mount == null) {
			throw new ClientException("Mount id is invalid [" + mountId + "]");
		}
		
		ContainerGraphServiceS2CApi graphService;
		try {
			graphService = containerConnectionService.getGraphServiceForMount(mount);
		} catch (ContainerConnectionException e) {
			throw new ClientException(e.getMessage());
		}	
		
		String mountPath = mount.getPath();		
		if (!graphPath.startsWith(mountPath + ".")) { //TODO this check should be in the container. 
			throw new ClientException("Graph path [" + graphPath + "], must start with mountPath [" + mountPath + ".]");
		}
		
		try {
			return graphService.addGraphItem(mountId, graphPath, graphItem, inputEventIds, outputEventId);
		}
		catch (ContainerGraphServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}
	
	@Override
	public void removeGraphItem(long id) {
		log.info("Remove from TODO [" + id + "]");
	}

	@Override
	public void setGraphValue(long id, long mountId,  int value) throws ClientException {
		log.info("Set Value [" + id + ", " + mountId + ", " + value + "]");
		
		MountDescriptor mount = topologyService.getMount(mountId); //TODO for speed, this should be a check of an in memory cache 
		if (mount == null) {
			throw new ClientException("Mount id is invalid [" + mountId + "]");
		}
		
		ContainerGraphServiceS2CApi graphService;
		try {
			graphService = containerConnectionService.getGraphServiceForMount(mount);
		} catch (ContainerConnectionException e) {
			throw new ClientException(e.getMessage());
		}			
		
		try {
			graphService.setGraphValue(id, value);
		}
		catch (ContainerGraphServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}
	
	@Override
	public void setPersisted(long id, long mountId, boolean persisted) throws ClientException {
		log.info("Set Persisted [" + id + ", " + mountId + ", " + persisted + "]");
		
		MountDescriptor mount = topologyService.getMount(mountId); //TODO for speed, this should be a check of an in memory cache 
		if (mount == null) {
			throw new ClientException("Mount id is invalid [" + mountId + "]");
		}
		
		ContainerGraphServiceS2CApi graphService;
		try {
			graphService = containerConnectionService.getGraphServiceForMount(mount);
		} catch (ContainerConnectionException e) {
			throw new ClientException(e.getMessage());
		}	
		
		try {
			graphService.setPersisted(id, persisted);
		}
		catch (ContainerGraphServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}


	@Override
	public void populateAllMounts() {
		Set<MountDescriptor> allMountItems = topologyService.getAllMounts();
		for (MountDescriptor mountDescriptor : allMountItems) {
			topologyService_mountCreatedNotification(mountDescriptor);
			if (mountDescriptor.getHostContainer() != null) {
				topologyService_mountHostedNotification(mountDescriptor);
			}
		}
	}
	
	@Override
	public void registerForUpdatesFrom(long mountId) throws ClientException {
		//TODO this needs actually to register specific paths for updates, and keep in client. 
		// TODO as a temporary bodge we dont actually register, but just return everything. 
		
		// check we have a container mapping for the specified mount 
		MountDescriptor mount = topologyService.getMount(mountId); //TODO for speed, this should be a check of an in memory cache 
		if (mount == null) {
			throw new ClientException("Mount id is invalid [" + mountId + "]");
		}
		
		ContainerGraphServiceS2CApi graphService;
		try {
			graphService = containerConnectionService.getGraphServiceForMount(mount);
		} catch (ContainerConnectionException e) {
			throw new ClientException(e.getMessage());
		}	
		
		graphService.populateAllGraphItems(mountId);
	}
	

	@Override
	public void containerGraphService_graphItemCreated(long id, String path, String calculation, String outputEventId) {
		//TODO we need to be able to filter the graph updates on a per client basis. The client should only get a subset. A seperate service me things
		getOutgoingS2CService().clientGraphService_graphItemCreated(id,  path, calculation, outputEventId);	
	}

	@Override
	public void monitorEvent_updateEventNotification(Event event) {
		//TODO we need to be able to filter the graph updates on a per client basis. The client should only get a subset. A seperate service me things
		if (!getConnection().isStoppedOrStopping()) {
			getOutgoingS2CService().clientGraphService_eventValueUpdated(event.getId(), ((IntEvent)event).getValue());	//TODO should event monitoring be in a different service to client?
		}
	}
	
	@Override
	public void topologyService_machineCreatedNotification(MachineDescriptor machineDescriptor) {
	}

	@Override
	public void topologyService_machineDeletedNotification(MachineDescriptor machineDescriptor) {
	}

	@Override
	public void topologyService_hubCreatedNotification(HubDescriptor hubDescriptor) {
	}

	@Override
	public void topologyService_hubSetListenPort(HubDescriptor hubDescriptor) {
	}

	@Override
	public void topologyService_hubDeletedNotification(HubDescriptor hubDescriptor) {
	}

	@Override
	public void topologyService_containerCreatedNotification(ContainerDescriptor containerDescriptor) {
	}

	@Override
	public void topologyService_containerDeletedNotification(ContainerDescriptor containerDescriptor) {
	}

	@Override
	public void topologyService_mountCreatedNotification(MountDescriptor mountDescriptor) {
		getOutgoingS2CService().clientGraphService_mountCreated(mountDescriptor.getId(), mountDescriptor.getPath());
	}

	@Override
	public void topologyService_mountDeletedNotification(MountDescriptor mountDescriptor) {
	}

	@Override
	public void topologyService_mountHostedNotification(MountDescriptor mountDescriptor) {
		getOutgoingS2CService().clientGraphService_mountHosted(mountDescriptor.getId(), mountDescriptor.getHostContainer().getId());
	}

	@Override
	public void topologyService_mountDehostedNotification(MountDescriptor mountDescriptor) {
		getOutgoingS2CService().clientGraphService_mountDehosted(mountDescriptor.getId());
	}
	
	@Override
	public void connection_notifyStarted(Connection connection) {
		// TODO should we register here?
	}

	@Override
	public void connection_notifyStopped(Connection connection, boolean expected) {
		// The connection has shut off, so de-register
		topologyService.deregister(this);
		containerGraphService.deregister(this);
		containerMonitorEventService.deregister(this);
	}
	
	public void setTopologyService(TopologyService topologyService) {
		this.topologyService = topologyService;
		topologyService.register(this);
	}

	public void setContainerConnectionService(ContainerConnectionService containerConnectionService) {
		this.containerConnectionService = containerConnectionService;
	}
	
	public void setContainerGraphService(ContainerGraphService containerGraphService) {
		this.containerGraphService = containerGraphService;
		containerGraphService.register(this);
	}

	public void setContainerMonitorEventService(
			ContainerMonitorEventService containerMonitorEventService) {
		this.containerMonitorEventService = containerMonitorEventService;
		containerMonitorEventService.register(this);
	}


}
