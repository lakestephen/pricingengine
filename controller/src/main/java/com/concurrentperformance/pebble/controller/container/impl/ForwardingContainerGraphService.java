package com.concurrentperformance.pebble.controller.container.impl;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.concurrentperformance.pebble.comms.rpc.server.service.RpcServerS2CSupport;
import com.concurrentperformance.pebble.comms.rpc.server.service.SkelitalRpcServerS2CSupport;
import com.concurrentperformance.pebble.controller.container.ContainerGraphService;
import com.concurrentperformance.pebble.controller.container.ContainerGraphServiceListener;
import com.concurrentperformance.pebble.controllercontainer.api.graph.ContainerGraphServiceC2SApi;
import com.concurrentperformance.pebble.controllercontainer.api.graph.ContainerGraphServiceS2CApi;

public class ForwardingContainerGraphService extends SkelitalRpcServerS2CSupport<ContainerGraphServiceS2CApi>
		implements 	ContainerGraphServiceC2SApi, 
					RpcServerS2CSupport<ContainerGraphServiceS2CApi>, 
					ContainerGraphService {

	private Set<ContainerGraphServiceListener> listeners = new CopyOnWriteArraySet<ContainerGraphServiceListener>(); 

	@Override
	public void graphService_graphItemCreated(long id, String path, String calculation, String outputEventId) {
		for (ContainerGraphServiceListener listener : listeners) {
			listener.containerGraphService_graphItemCreated(id, path, calculation, outputEventId);
		}		
	}
	
	@Override
	public void register(ContainerGraphServiceListener listener) { 
		listeners.add(listener);		
	}

	@Override
	public void deregister(ContainerGraphServiceListener listener) {
		listeners.remove(listener);		
	}
}
