package com.concurrentperformance.pebble.controller.functional.container.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controller.functional.container.exception.ContainerConnectionException;
import com.concurrentperformance.pebble.controller.functional.container.service.ContainerConnectionService;
import com.concurrentperformance.pebble.controller.functional.topology.beans.ContainerDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyDescriptor;
import com.concurrentperformance.pebble.controller.functional.topology.beans.TopologyType;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorService;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorServiceListener;
import com.concurrentperformance.pebble.controller.functional.topology.service.TopologyMonitorState;
import com.concurrentperformance.pebble.controllercontainer.api.graph.ContainerGraphServiceS2CApi;

public class DefaultContainerConnectionService implements
		ContainerConnectionService, TopologyMonitorServiceListener {

	private final Log log = LogFactory.getLog(this.getClass());
	
	private final ConcurrentMap<Long, RpcConnection> connections = new ConcurrentHashMap<Long, RpcConnection>(); 
	
	
	@Override
	public ContainerGraphServiceS2CApi getGraphServiceForMount(MountDescriptor mount) throws ContainerConnectionException {
		// TODO UUGH this nasty looping is temporary code to get demo working. 
		// Will need to be replaced with an asynch call that registers a listener for the response, and then queue the task. 
		ContainerGraphServiceS2CApi graphService;
		int retry = 5;
		while(true) {
			try {
				graphService = doGetGraphServiceForMount(mount);
				return graphService;
			} catch (ContainerConnectionException e) {
				if (retry > 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else { 
					throw e;
				}
				retry--;
			}			
		}		
	}
	
	private ContainerGraphServiceS2CApi doGetGraphServiceForMount(MountDescriptor mount) throws ContainerConnectionException {
		ContainerDescriptor container = mount.getHostContainer(); //TODO for speed, this (and all calls leading into it) must be a check of an in memory cache - pass in an long id
		if (container == null) {
			throw new ContainerConnectionException("Mount [" + mount + "] does not have a host container");
		}
		
		RpcConnection connection = connections.get(container.getId());
		if (connection == null) {
			throw new ContainerConnectionException("Mount [" + mount + "] does not have a current connection");
		}

		ContainerGraphServiceS2CApi graphService = connection.getService(ContainerGraphServiceS2CApi.class);
		return graphService;
	}

	@Override
	public void topologyMonitorService_connectionRegistration(
			TopologyDescriptor topologyDescriptor, RpcConnection connection) {

		if (topologyDescriptor.getType() == TopologyType.CONTAINER) {
			log.info("Connection Registration [" + connection + "] for container [" + topologyDescriptor + "]");
			connections.put(topologyDescriptor.getId(), connection);
		}		
	}
	
	@Override
	public void topologyMonitorService_connectionDeregistration(TopologyDescriptor topologyDescriptor) {
		if (topologyDescriptor.getType() == TopologyType.CONTAINER) {
			log.info("Connection Deregistration for container [" + topologyDescriptor + "]");
			connections.remove(topologyDescriptor.getId());
		}		
	}

	@Override
	public void topologyMonitorService_updateTopologyStatus(
			TopologyDescriptor topologyDescriptor,TopologyMonitorState topologyState) {
	}
	
	public final void setTopologyMonitorService(TopologyMonitorService topologyMonitorService) {
		topologyMonitorService.register(this);
	}
}
