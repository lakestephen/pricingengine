package com.concurrentperformance.pebble.controller.functional.topology.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.HubDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MachineDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.dao.TopologyDao;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class HibernateTopologyDao extends HibernateDaoSupport implements TopologyDao {

	@Override
	public void persistTopology(TopologyDescriptor topologyDescriptor) {
		getHibernateTemplate().saveOrUpdate(topologyDescriptor);
	}

	@Override
	public void deleteTopology(TopologyDescriptor existingTopologyDescriptor) {
		getHibernateTemplate().delete(existingTopologyDescriptor);		
	}
	
	@Override
	public void deleteContainer(ContainerDescriptor existingContainerDescriptor) {
//		existingContainerDescriptor.removeAllMounts();
//		existingContainerDescriptor.getParentHub().removeContainerDescriptor(existingContainerDescriptor);
		getHibernateTemplate().delete(existingContainerDescriptor);		
	}



	@Override
	public TopologyDescriptor findTopologyById(long topologyId) {
		TopologyDescriptor topologyDescriptor = null;
		
		@SuppressWarnings("unchecked")
		List<ContainerDescriptor> list = getHibernateTemplate().find("from TopologyDescriptor topologyDescriptor where id = ?", topologyId); 

		if (list != null && list.size() > 0) {
			topologyDescriptor = list.get(0);
		}
		
		return topologyDescriptor;
	}

	@Override
	public Set<MachineDescriptor> getAllMachine() {
		Set<MachineDescriptor> machineDescriptors = null;
		
		@SuppressWarnings("unchecked")
		List<MachineDescriptor> list = getHibernateTemplate().find("from MachineDescriptor machineDescriptor"); 
		
		machineDescriptors = new HashSet<MachineDescriptor>(list);
		
		return machineDescriptors;
	}

	@Override
	public MachineDescriptor findMachineById(long machineId) {
		MachineDescriptor machineDescriptor = null;
		
		@SuppressWarnings("unchecked")
		List<MachineDescriptor> list = getHibernateTemplate().find("from MachineDescriptor machineDescriptor where id = ?", machineId); 

		if (list != null && list.size() > 0) {
			machineDescriptor = list.get(0);
		}
		
		return machineDescriptor;
	}
	
	@Override
	public Set<HubDescriptor> getAllHubs(MachineDescriptor parentMachineDescriptor) {
		Set<HubDescriptor> hubDescriptors = null;
		
		@SuppressWarnings("unchecked")
		List<HubDescriptor> list = getHibernateTemplate().find("from HubDescriptor hubDescriptor where parentMachine = ?", parentMachineDescriptor); 
		
		hubDescriptors = new HashSet<HubDescriptor>(list);
		
		return hubDescriptors;
	}

	@Override
	public HubDescriptor findHubById(long hubId) {
		HubDescriptor hubDescriptor = null;
		
		@SuppressWarnings("unchecked")
		List<HubDescriptor> list = getHibernateTemplate().find("from HubDescriptor hubDescriptor where id = ?", hubId); 

		if (list != null && list.size() > 0) {
			hubDescriptor = list.get(0);
		}
		
		return hubDescriptor;
	}
	
	@Override
	public Set<ContainerDescriptor> getAllContainers(HubDescriptor parentHubDescriptor) {
		Set<ContainerDescriptor> containerDescriptors = null;
		
		@SuppressWarnings("unchecked")
		List<ContainerDescriptor> list = getHibernateTemplate().find("from ContainerDescriptor containerDescriptor where parentHub = ?", parentHubDescriptor); 
		
		containerDescriptors = new HashSet<ContainerDescriptor>(list);
		
		return containerDescriptors;
	}

	@Override
	public Set<ContainerDescriptor> getAllContainers() {
		Set<ContainerDescriptor> containerDescriptors = null;
		
		@SuppressWarnings("unchecked")
		List<ContainerDescriptor> list = getHibernateTemplate().find("from ContainerDescriptor containerDescriptor "); 
		
		containerDescriptors = new HashSet<ContainerDescriptor>(list);
		
		return containerDescriptors;
	}
		
	@Override
	public ContainerDescriptor findContainerById(long containerId) {
		ContainerDescriptor containerDescriptor = null;
		
		@SuppressWarnings("unchecked")
		List<ContainerDescriptor> list = getHibernateTemplate().find("from ContainerDescriptor containerDescriptor where id = ?", containerId); 

		if (list != null && list.size() > 0) {
			containerDescriptor = list.get(0);
		}
		
		return containerDescriptor;
	}


	@Override
	public MountDescriptor findMountById(long mountId) {
		MountDescriptor mountDescriptor = null;
		
		@SuppressWarnings("unchecked")
		List<MountDescriptor> list = getHibernateTemplate().find("from MountDescriptor mountDescriptor where id = ?", mountId); 

		if (list != null && list.size() > 0) {
			mountDescriptor = list.get(0);
		}
		
		return mountDescriptor;
	}
	
	@Override
	public Set<MountDescriptor> getAllMountItems(ContainerDescriptor hostContainer) {
		Set<MountDescriptor> mountDescriptors = null;
		
		@SuppressWarnings("unchecked")
		List<MountDescriptor> list = getHibernateTemplate().find("from MountDescriptor  mountDescriptor  where hostContainer = ?", hostContainer); 

		mountDescriptors = new HashSet<MountDescriptor>(list);
		
		return mountDescriptors;
		
	}	

	@Override
	public Set<MountDescriptor> getAllMountItems() {
		Set<MountDescriptor> mountDescriptors = null;
		
		@SuppressWarnings("unchecked")
		List<MountDescriptor> list = getHibernateTemplate().find("from MountDescriptor  mountDescriptor "); 

		mountDescriptors = new HashSet<MountDescriptor>(list);
		
		return mountDescriptors;
		
	}

}