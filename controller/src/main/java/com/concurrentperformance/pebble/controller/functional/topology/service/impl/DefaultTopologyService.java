package com.concurrentperformance.pebble.controller.functional.topology.service.impl;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.dao.TopologyDao;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyServiceListener;
import com.concurrentperformance.pebble.controller.functional.topology.service.exception.TopologyServiceException;
import com.concurrentperformance.pebble.util.service.AsynchServiceListenerSupport;
import com.concurrentperformance.pebble.util.service.ServiceListenerSupport;

public class DefaultTopologyService extends AsynchServiceListenerSupport<TopologyServiceListener>  
		implements TopologyService, ServiceListenerSupport<TopologyServiceListener>{


	private static final String DISALLOWED_CHARACTERS = " !\\/\"£$%^&*()<>?,@'~#{}/[/]|";

	private final Log log = LogFactory.getLog(this.getClass());
	
	private TopologyDao topologyDao;
	
	@Override
	public MachineDescriptor createMachine(String machineName) throws TopologyServiceException {
		log.info("Create machine [" + machineName + "]");
		checkName(machineName, "Machine name");

		MachineDescriptor machineDescriptor = new MachineDescriptor();
		machineDescriptor.setName(machineName);
		
		try {
			topologyDao.persistTopology(machineDescriptor);
			fireMachineCreated(machineDescriptor);
			return machineDescriptor;
		}
		catch (DataIntegrityViolationException e) {
			throw new TopologyServiceException("Can't create Machine [" + machineName + "] as name already exists. (Machines/Hubs/Containers/Mounts Must all be globally unique)");
		}
		
	}

	private void fireMachineCreated(final MachineDescriptor machineDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_machineCreatedNotification(machineDescriptor);
				} 
			}			
		});		
	}

	@Override
	public void deleteMachine(long machineId) throws TopologyServiceException {
		log.info("Delete machine [" + machineId + "]");
		MachineDescriptor existingMachineDescriptor = topologyDao.findMachineById(machineId);
		
		if (existingMachineDescriptor == null) { 
			throw new TopologyServiceException("Delete of machine [" + machineId + "] failed as it does not exist.");
		}
		
		if (existingMachineDescriptor.getHubDescriptors().size() > 0 ) {
			throw new TopologyServiceException("Delete of machine [" + machineId + "] failed as it still has child hubs.");
		}
		
		topologyDao.deleteTopology(existingMachineDescriptor);

		fireMachineDeleted(existingMachineDescriptor);
	}

	private void fireMachineDeleted(final MachineDescriptor machineDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_machineDeletedNotification(machineDescriptor);
				} 
			}			
		});
	}
	
	@Override
	public Set<MachineDescriptor> getAllMachines() {
		Set<MachineDescriptor> machines = topologyDao.getAllMachine();
		return machines;		
	}

	@Override
	public HubDescriptor createHub(long parentMachineId, String hubName) throws TopologyServiceException {
		log.info("Create hub [" + parentMachineId + "->" + hubName + "]");
		// check we are not using invalid characters
		checkName(hubName, "Hub name");
		
		// find the parent machine to add to.
		MachineDescriptor paretMachineDescriptor = topologyDao.findMachineById(parentMachineId);
		if (paretMachineDescriptor == null) {
			throw new TopologyServiceException("Machine [" + parentMachineId + "] not found.");
		}
		
		//create the new hub descriptor and persist
		HubDescriptor hubDescriptor = new HubDescriptor();
		hubDescriptor.setName(hubName);
		paretMachineDescriptor.addHubDescriptor(hubDescriptor);
		
		try {
			topologyDao.persistTopology(paretMachineDescriptor);
			fireHubCreated(hubDescriptor);
			return hubDescriptor;
		}
		catch (DataIntegrityViolationException e) {
			throw new TopologyServiceException("Can't create Hub [" + hubName + "] as name already exists. (Machines/Hubs/Containers/Mounts Must all be globally unique)");
		}
	}
	
	private void fireHubCreated(final HubDescriptor hubDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {				
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_hubCreatedNotification(hubDescriptor);
				} 
			}			
		});
	}
	
	@Override
	public void setHubListenPort(long hubId, int hubListenPort) throws TopologyServiceException {
		HubDescriptor existingHubDescriptor = topologyDao.findHubById(hubId);
		if (existingHubDescriptor == null) { 
			throw new TopologyServiceException("Cant set listen port for hub [" + hubId + "] as it does not exist.");
		}

		existingHubDescriptor.setListenPort(hubListenPort);
		topologyDao.persistTopology(existingHubDescriptor);
		fireHubSetListenPort(existingHubDescriptor);
	}
	
	private void fireHubSetListenPort(final HubDescriptor hubDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_hubSetListenPort(hubDescriptor);
				} 
			}			
		});
	}
	
	@Override
	public void deleteHub(long hubId) throws TopologyServiceException {
		log.info("Delete hub [" + hubId + "]");

		HubDescriptor existingHubDescriptor = topologyDao.findHubById(hubId);
		
		if (existingHubDescriptor == null) { 
			throw new TopologyServiceException("Delete of hub id [" + hubId + "] failed as it does not exist.");
		}
		
		if (existingHubDescriptor.getContainerDescriptors().size() > 0 ) {
			throw new TopologyServiceException("Delete of hub [" + hubId + "] failed as it still has child containers.");
		}

		topologyDao.deleteTopology(existingHubDescriptor);

		fireHubDeleted(existingHubDescriptor);
	}

	private void fireHubDeleted(final HubDescriptor hubDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_hubDeletedNotification(hubDescriptor);
				} 
			}			
		});
	}

	@Override
	public Set<HubDescriptor> getAllHubs(MachineDescriptor parentMachine) {
		Set<HubDescriptor> machines = topologyDao.getAllHubs(parentMachine);
		return machines;		
	}
	
	@Override
	public HubDescriptor getHub(long hubId) {
		HubDescriptor hub = topologyDao.findHubById(hubId);
		return hub;
	}
		
	@Override
	public ContainerDescriptor createContainer(long parentHubId, String containerName) throws TopologyServiceException {
		log.info("Create contaier [" + parentHubId + "->" + containerName + "]");
		// check we are not using invalid characters
		checkName(containerName, "Container name");
		
		// find the parent hub to add to. 
		HubDescriptor parentHubDescriptor = topologyDao.findHubById(parentHubId);
		if (parentHubDescriptor == null) {
			throw new TopologyServiceException("Hub [" + parentHubId + "] not found.");
		}
		
		//create the new container and persist
		ContainerDescriptor containerDescriptor = new ContainerDescriptor();
		containerDescriptor.setName(containerName);
		parentHubDescriptor.addContainerDescriptor(containerDescriptor);	
		
		try {
			topologyDao.persistTopology(parentHubDescriptor);
			fireContainerCreated(containerDescriptor);
			return containerDescriptor;
		}
		catch (DataIntegrityViolationException e) {
			throw new TopologyServiceException("Can't create Container [" + containerName + "] as name already exists. (Machines/Hubs/Containers/Mounts Must all be globally unique)");
		}		
		
	}
	
	private void fireContainerCreated(final ContainerDescriptor containerDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_containerCreatedNotification(containerDescriptor);				} 
			}			
		});
	}

	@Override
	public void deleteContainer(long containerId) throws TopologyServiceException {
		log.info("Delete container [" + containerId + "]");

		ContainerDescriptor existingContainerDescriptor = topologyDao.findContainerById(containerId);
		
		if (existingContainerDescriptor == null) { 
			throw new TopologyServiceException("Delete of container [" + containerId + "] failed as it does not exist.");
		}

		// first remove the mounts so the cascade does not delete them 
		Set<MountDescriptor> hostedMounts = existingContainerDescriptor.getHostedMounts();
		for (MountDescriptor mount : hostedMounts) {
			doDehostMount(mount);
		}
		
		topologyDao.deleteContainer(existingContainerDescriptor);
		
		fireContainerDeleted(existingContainerDescriptor);
	}

	private void fireContainerDeleted(final ContainerDescriptor containerDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_containerDeletedNotification(containerDescriptor);				} 
			}			
		});
	}
	
	@Override
	public Set<ContainerDescriptor> getAllContainers(HubDescriptor parentHubDescriptor) {
		Set<ContainerDescriptor> containers = topologyDao.getAllContainers(parentHubDescriptor);
		return containers;		
	}
	
	@Override
	public ContainerDescriptor getContainer(long containerId) {
		ContainerDescriptor container = topologyDao.findContainerById(containerId);
		return container;
	}


	@Override
	public MountDescriptor createMount(String mountPath) throws TopologyServiceException {
		log.info("Create mount [" + mountPath + "]");
		// check we are not using invalid characters
		checkName(mountPath, "Mount name");
		
		/// A mount must either be at the root, or below another mount
		if (!isRootMount(mountPath)) {
			Set<MountDescriptor> allMounts = topologyDao.getAllMountItems();
			boolean isBelowAnotherMount = false;
			for (MountDescriptor existingMount : allMounts) {
				if (mountPath.startsWith(existingMount.getName() + ".")) {
					isBelowAnotherMount = true;
					break;
				}
			}
			
			if (!isBelowAnotherMount) {
				throw new TopologyServiceException("Mount [" + mountPath + "] is neither root (no dot) or subordinate (under another mount)");
			}
		}
		
		//create the new container and persist
		MountDescriptor mountDescriptor = new MountDescriptor();
		mountDescriptor.setName(mountPath);
		
		try {
			topologyDao.persistTopology(mountDescriptor);
			fireMountCreated(mountDescriptor);
			return mountDescriptor;
		}
		catch (DataIntegrityViolationException e) {
			throw new TopologyServiceException("Can't create Mount [" + mountPath + "] as name already exists. (Machines/Hubs/Containers/Mounts Must all be globally unique)");
		}		
		
	}
	
	@Override
	public Set<ContainerDescriptor> getAllContainers() {
		// SJL Auto-generated method stub
		return topologyDao.getAllContainers();
	}

	private boolean isRootMount(String mountPath) {
		boolean rootMount = !(mountPath.contains("."));
		return rootMount;
	}

	private void fireMountCreated(final MountDescriptor mountDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_mountCreatedNotification(mountDescriptor);				} 
			}			
		});
	}

	@Override
	public void deleteMount(long mountId) throws TopologyServiceException {
		log.info("Delete mount [" + mountId + "]");

		MountDescriptor existingMountDescriptor = topologyDao.findMountById(mountId);
		
		if (existingMountDescriptor == null) { 
			throw new TopologyServiceException("Delete of mount [" + mountId + "] failed as it does not exist.");
		}
		
		topologyDao.deleteTopology(existingMountDescriptor);
		
		fireMountDeleted(existingMountDescriptor);
	}

	private void fireMountDeleted(final MountDescriptor mountDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_mountDeletedNotification(mountDescriptor);				} 
			}			
		});
	}
	
	@Override
	public void hostMountInContainer(long mountId, long hostContainerId) throws TopologyServiceException {
		log.info("Host mount [" + mountId + "]");
		MountDescriptor existingMountDescriptor = topologyDao.findMountById(mountId);
		
		if (existingMountDescriptor == null) { 
			throw new TopologyServiceException("Host of mount [" + mountId + "] failed as it does not exist.");
		}
		
		ContainerDescriptor hostContainerDescriptor = topologyDao.findContainerById(hostContainerId);
		
		if (hostContainerDescriptor == null) { 
			throw new TopologyServiceException("Host of mount [" + existingMountDescriptor + "] failed as host container [" + hostContainerId + "] does not exist.");
		}
		
		hostContainerDescriptor.addHostedMount(existingMountDescriptor);
		topologyDao.persistTopology(hostContainerDescriptor);

		fireMountHosted(existingMountDescriptor);
	}

	private void fireMountHosted(final MountDescriptor mountDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_mountHostedNotification(mountDescriptor);				} 
			}			
		});
	}

	@Override
	public void dehostMount(long mountId) throws TopologyServiceException {
		log.info("Unhost mount [" + mountId + "]");
		MountDescriptor existingMountDescriptor = topologyDao.findMountById(mountId);
		
		if (existingMountDescriptor == null) { 
			throw new TopologyServiceException("Host of mount [" + mountId + "] failed as it does not exist.");
		}				
		
		doDehostMount(existingMountDescriptor);
	}

	private void doDehostMount(MountDescriptor existingMountDescriptor) throws TopologyServiceException {
		ContainerDescriptor parentContainer = existingMountDescriptor.getHostContainer();
		
		if (parentContainer == null) { 
			throw new TopologyServiceException("Unhost of mount [" + existingMountDescriptor + "] failed as no parent container.");
		}

		parentContainer.removeHostedMount(existingMountDescriptor);
		topologyDao.persistTopology(existingMountDescriptor);

		fireMountDehosted(existingMountDescriptor);
	}

	private void fireMountDehosted(final MountDescriptor mountDescriptor) {
		submitTask(new Runnable(){
			@Override
			public void run() {
				for (TopologyServiceListener listener : getListeners()) {
					listener.topologyService_mountDehostedNotification(mountDescriptor);				} 
			}			
		});
	}

	@Override
	public MountDescriptor getMount(long mountId) {
		MountDescriptor mount = topologyDao.findMountById(mountId);
		return mount;
	}

	@Override
	public Set<MountDescriptor> getAllMounts(ContainerDescriptor hostContainer) {
		Set<MountDescriptor> mounts = topologyDao.getAllMountItems(hostContainer);
		return mounts;
	}

	@Override
	public Set<MountDescriptor> getAllMounts() {
		Set<MountDescriptor> mounts = topologyDao.getAllMountItems();
		return mounts;
	}
	
	@Override
	public String getTopologyName(long topologyId) {
		TopologyDescriptor topologyDescriptor = topologyDao.findTopologyById(topologyId);
		String name = topologyDescriptor.getName();
		return name;
	}

	private void checkName(String name, String typeForLogging) throws TopologyServiceException {
		if (name.matches(".*[" + DISALLOWED_CHARACTERS + "].*")) { //TODO put this check in common service
			String msg = typeForLogging + " [" + name + "] contains illegal characters It can't contain any of [" + DISALLOWED_CHARACTERS + "].";
			log.warn(msg);
			throw new TopologyServiceException(msg);
		}
	}
	
	public final void setTopologyDao(TopologyDao topologyDao) {
		this.topologyDao = topologyDao;
	}

}
