package com.concurrentperformance.pebble.controller.container.impl;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.rpc.server.service.RpcServerS2CSupport;
import com.concurrentperformance.pebble.comms.rpc.server.service.SkelitalRpcServerS2CSupport;
import com.concurrentperformance.pebble.controller.container.ContainerMonitorEventService;
import com.concurrentperformance.pebble.controller.container.ContainerMonitorEventServiceListener;
import com.concurrentperformance.pebble.controllercontainer.api.monitorevent.ContainerMonitorEventServiceC2SApi;
import com.concurrentperformance.pebble.controllercontainer.api.monitorevent.ContainerMonitorEventServiceS2CApi;
import com.concurrentperformance.pebble.msgcommon.event.Event;

public class ForwardingContainerMonitorEventService extends SkelitalRpcServerS2CSupport<ContainerMonitorEventServiceS2CApi>
		implements ContainerMonitorEventServiceC2SApi, 
				   RpcServerS2CSupport<ContainerMonitorEventServiceS2CApi>,
				   ContainerMonitorEventService {

	private final Log log = LogFactory.getLog(this.getClass());

	private Set<ContainerMonitorEventServiceListener> listeners = new CopyOnWriteArraySet<ContainerMonitorEventServiceListener>(); 

	@Override
	public void monitorEvent_updateEventNotification(Event event) {
		for (ContainerMonitorEventServiceListener listener : listeners) {
			listener.monitorEvent_updateEventNotification(event);
		}		
	}


	
	@Override
	public void register(ContainerMonitorEventServiceListener listener) { 
		listeners.add(listener);		
	}

	@Override
	public void deregister(ContainerMonitorEventServiceListener listener) {
		listeners.remove(listener);		
	}
}
