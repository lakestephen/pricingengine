package com.concurrentperformance.pebble.controller.client;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.comms.rpc.server.service.RpcServerS2CSupport;
import com.concurrentperformance.pebble.comms.rpc.server.service.SkelitalRpcServerS2CSupport;
import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorServiceListener;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorState;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyServiceListener;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.TopologyServiceException;
import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceC2SApi;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceS2CApi;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientApplicationDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientContainerDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientHubDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMachineDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMountDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyState;

/**
 * Class to orchestrate the controller side of the client-controller interface for 
 * the topology services. 
 * 1) Listens for calls from the client api, and delegates them to the controller 
 * topology service. 
 * 2) Listens to and keeps a set of the connecting and disconnecting clients for notifications
 * 3) Listens to updates from the controller topology service and passes them back 
 * over the wire to the clients 
 *
 * @author Stephen Lake
 */
//TODO rename
//TODO we have a separate service per client because we will want to hold specific client state in future.
//TODO we *could* register the proxy directly with the service. 
public class DefaultClientTopologyService extends SkelitalRpcServerS2CSupport<ClientTopologyServiceS2CApi> 
		implements ClientTopologyServiceC2SApi, 
		RpcServerS2CSupport<ClientTopologyServiceS2CApi>,	TopologyServiceListener, 
		TopologyMonitorServiceListener,	ConnectionListener {

	private final Log log = LogFactory.getLog(this.getClass());

	private TopologyService topologyService;
	private TopologyMonitorService topologyMonitorService;
	
	@Override
	public void populate() {
		ClientApplicationDetails clientApplicationDetails = new ClientApplicationDetails(-2,-1);
		clientApplicationDetails.setName("<Bond Pricing>");
		getOutgoingS2CService().clientTopologyService_applicationCreated(clientApplicationDetails);

		Set<MachineDescriptor> machines = topologyService.getAllMachines();
		for (MachineDescriptor machine : machines) {
			topologyService_machineCreatedNotification(machine);
			
			Set<HubDescriptor> hubs = topologyService.getAllHubs(machine);
			for (HubDescriptor hub : hubs) {
				topologyService_hubCreatedNotification(hub);
				
				Set<ContainerDescriptor> containers = topologyService.getAllContainers(hub);
				for (ContainerDescriptor container : containers) {
					topologyService_containerCreatedNotification(container);
					
					Set<MountDescriptor> mounts = topologyService.getAllMounts(container);
					for (MountDescriptor mount : mounts) {
						topologyService_mountCreatedNotification(mount);
					}

				}
			}
		}
	}

	
	@Override
	public long createMachine(String machineName) throws ClientException {
		try {
			return topologyService.createMachine(machineName).getId();
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}

	@Override
	public void deleteMachine(long machineId) throws ClientException {
		try {
			topologyService.deleteMachine(machineId);
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}
	
	@Override
	public long createHub(long parentMachineId, String hubName) throws ClientException {
		try {
			return topologyService.createHub(parentMachineId, hubName).getId();
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}	


	@Override
	public void setHubListenPort(long hubId, int hubListenPort) throws ClientException {
		try {
			topologyService.setHubListenPort(hubId, hubListenPort);
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}

	@Override
	public void deleteHub(long hubId) throws ClientException {
		try {
			topologyService.deleteHub(hubId);
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}

	@Override
	public long createContainer(long parentHubId, String containerName) throws ClientException {
		try {
			return topologyService.createContainer(parentHubId, containerName).getId();
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}

	@Override
	public void deleteContainer(long containerId) throws ClientException {
		try {
			topologyService.deleteContainer(containerId);
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}
	
	@Override
	public Set<ClientContainerDetails> getAllContainers() {
		Set<ClientContainerDetails> clientContainers = new HashSet<ClientContainerDetails>(); 
		Set<ContainerDescriptor> allContainers = topologyService.getAllContainers();
		for (ContainerDescriptor containerDescriptor : allContainers) {
			ClientContainerDetails clientContainer = buildClientContainerDetails(containerDescriptor);
			clientContainers.add(clientContainer);
		}
		
		return clientContainers;
	}

	@Override
	public long createMount(String mountName) throws ClientException {
		try {
			return topologyService.createMount(mountName).getId();
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}

	@Override
	public void deleteMount(long mountId) throws ClientException {
		try {
			topologyService.deleteMount(mountId);
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}

	@Override
	public void hostMountInContainer(long mountId, long hostContainerId) throws ClientException {
		try {
			topologyService.hostMountInContainer(mountId, hostContainerId);
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}
	
	@Override
	public void dehostMount(long mountId) throws ClientException {
		try {
			topologyService.dehostMount(mountId);
		}
		catch (TopologyServiceException e) {
			throw new ClientException(e.getMessage());
		}
	}
	
	@Override
	public void topologyService_machineCreatedNotification(MachineDescriptor machineDescriptor) {
		ClientMachineDetails clientMachine = buildClientMachineDetails(machineDescriptor);
		getOutgoingS2CService().clientTopologyService_machineCreated(clientMachine);
	}

	@Override
	public void topologyService_machineDeletedNotification(MachineDescriptor machineDescriptor) {
		ClientMachineDetails clientMachine = buildClientMachineDetails(machineDescriptor);
		getOutgoingS2CService().clientTopologyService_machineDeleted(clientMachine);
	}
	
	private ClientMachineDetails buildClientMachineDetails(MachineDescriptor machineDescriptor) {
		ClientMachineDetails clientMachine = new ClientMachineDetails(machineDescriptor.getId(), -1);
		clientMachine.setName(machineDescriptor.getName());
		setClientTopologyState(machineDescriptor, clientMachine);
		return clientMachine;
	}


	@Override
	public void topologyService_hubCreatedNotification(HubDescriptor hubDescriptor) {
		ClientHubDetails clientHubDetails = buildClientHubDetails(hubDescriptor);		
		getOutgoingS2CService().clientTopologyService_hubCreated(clientHubDetails);
	}

	@Override
	public void topologyService_hubSetListenPort(HubDescriptor hubDescriptor) {
		ClientHubDetails clientHubDetails = buildClientHubDetails(hubDescriptor);		
		getOutgoingS2CService().clientTopologyService_hubUpdated(clientHubDetails);
	}

	@Override
	public void topologyService_hubDeletedNotification(HubDescriptor hubDescriptor) {
		ClientHubDetails clientHubDetails = buildClientHubDetails(hubDescriptor);		
		getOutgoingS2CService().clientTopologyService_hubDeleted(clientHubDetails);
	}

	private ClientHubDetails buildClientHubDetails(HubDescriptor hubDescriptor) {
		ClientHubDetails clientHubDetails = new ClientHubDetails(hubDescriptor.getId(), 
						hubDescriptor.getParentMachine().getId());
		clientHubDetails.setName(hubDescriptor.getName());
		clientHubDetails.setListenPort(hubDescriptor.getListenPort());
		setClientTopologyState(hubDescriptor, clientHubDetails);
		return clientHubDetails;
	}

	@Override
	public void topologyService_containerCreatedNotification(ContainerDescriptor containerDescriptor) {
		ClientContainerDetails clientContainerDetails = buildClientContainerDetails(containerDescriptor);		
		getOutgoingS2CService().clientTopologyService_containerCreated(clientContainerDetails);
	}

	@Override
	public void topologyService_containerDeletedNotification(ContainerDescriptor containerDescriptor) {
		ClientContainerDetails clientContainerDetails = buildClientContainerDetails(containerDescriptor);		
		getOutgoingS2CService().clientTopologyService_containerDeleted(clientContainerDetails);
	}
	
	@Override
	public void topologyMonitorService_updateTopologyStatus(TopologyDescriptor topologyDescriptor, TopologyMonitorState topologyState) {
		ClientTopologyState clientTopologyState = convertToClientTopologyState(topologyState);
		getOutgoingS2CService().clientTopologyService_updateTopologyState(topologyDescriptor.getId(), clientTopologyState);
	}
	
	@Override
	public void topologyMonitorService_connectionRegistration(TopologyDescriptor topologyDescriptor, RpcConnection connection) {		
	}
	
	@Override
	public void topologyMonitorService_connectionDeregistration(TopologyDescriptor topologyDescriptor) {
	}
	
	private ClientContainerDetails buildClientContainerDetails(ContainerDescriptor containerDescriptor) {
		long parentHubId = -1;
		if (containerDescriptor.getParentHub() !=  null) {
			parentHubId = containerDescriptor.getParentHub().getId();
		}
		ClientContainerDetails clientContainerDetails = new ClientContainerDetails(containerDescriptor.getId(), parentHubId);
		clientContainerDetails.setName(containerDescriptor.getName());
		setClientTopologyState(containerDescriptor, clientContainerDetails);
		return clientContainerDetails;
	}

	private void setClientTopologyState(TopologyDescriptor topologyDescriptor, ClientTopologyDetails topologyDetails) {
		TopologyMonitorState topologyMonitorState = topologyMonitorService.getTopologyState(topologyDescriptor.getId());
		topologyDetails.setClientTopologyState(convertToClientTopologyState(topologyMonitorState));
	}

	private ClientTopologyState convertToClientTopologyState(TopologyMonitorState topologyState) {
		ClientTopologyState clientTopologyState = null;
		if (topologyState != null) {
			// perform the translation between the internal state and what the client needs to know.
			switch (topologyState) {
				case RUNNING:
					clientTopologyState = ClientTopologyState.RUNNING;
					break;
				default:
					clientTopologyState = ClientTopologyState.STOPPED;
					break;
			}
		}
		return clientTopologyState;
	}	

	@Override
	public void topologyService_mountCreatedNotification(MountDescriptor mountDescriptor) {
		ClientMountDetails clientMountDetails = buildClientMountDetails(mountDescriptor);		
		getOutgoingS2CService().clientTopologyService_mountCreated(clientMountDetails);
	}

	@Override
	public void topologyService_mountHostedNotification(MountDescriptor mountDescriptor) {
		ClientMountDetails clientMountDetails = buildClientMountDetails(mountDescriptor);		
		getOutgoingS2CService().clientTopologyService_mountHosted(clientMountDetails);
	}

	@Override
	public void topologyService_mountDehostedNotification(MountDescriptor mountDescriptor) {
		ClientMountDetails clientMountDetails = buildClientMountDetails(mountDescriptor);		
		getOutgoingS2CService().clientTopologyService_mountDehosted(clientMountDetails);
	}
	@Override
	public void topologyService_mountDeletedNotification(MountDescriptor mountDescriptor) {
		ClientMountDetails clientMountDetails = buildClientMountDetails(mountDescriptor);		
		getOutgoingS2CService().clientTopologyService_mountDeleted(clientMountDetails);
	}

	private ClientMountDetails buildClientMountDetails(MountDescriptor mountDescriptor) {
		long hostContainerId = -1;
		if (mountDescriptor.getHostContainer() != null) {
			hostContainerId = mountDescriptor.getHostContainer().getId();
		}
		ClientMountDetails clientMountDetails = new ClientMountDetails(mountDescriptor.getId(), hostContainerId);
		
		clientMountDetails.setName(mountDescriptor.getName());
		return clientMountDetails;
	}

	
	public void setTopologyService(TopologyService topologyService) {
		this.topologyService = topologyService;
		topologyService.register(this);
	}

	public void setTopologyMonitorService(TopologyMonitorService topologyMonitorService) {
		this.topologyMonitorService = topologyMonitorService;
		topologyMonitorService.register(this);
	}

	@Override
	public void connection_notifyStarted(Connection connection) {
		// TODO should we register here?
	}

	@Override
	public void connection_notifyStopped(Connection connection, boolean expected) {
		// The connection has shut off, so de-register
		topologyService.deregister(this);
		topologyMonitorService.deregister(this);
	}
}
