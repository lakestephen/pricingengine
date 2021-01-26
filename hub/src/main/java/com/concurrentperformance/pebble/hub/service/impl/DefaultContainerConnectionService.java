package com.concurrentperformance.pebble.hub.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.server.socketlistener.ConnectionFactoryListener;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineConnection;
import com.concurrentperformance.pebble.comms.pipeline.server.socketlistener.PipelineServerConnectionFactory;
import com.concurrentperformance.pebble.hub.beans.impl.DefaultContainerProxy;
import com.concurrentperformance.pebble.hub.service.ContainerConnectionService;

public class DefaultContainerConnectionService implements ContainerConnectionService, ConnectionFactoryListener {

	private final Log log = LogFactory.getLog(this.getClass());

	private final ConcurrentMap<Long, DefaultContainerProxy> connections = new ConcurrentHashMap<Long, DefaultContainerProxy>(); 

	@Override
	public void connectionFactory_notifyNewConnection(Connection connection) {
		long containerId = connection.getWeAreConnectingToId();
		log.info("Adding connection [" + connection + "] for container [" + containerId + "]");
		DefaultContainerProxy container = getContainer(containerId);
		container.setConnection((PipelineConnection)connection);		
	}

	@Override
	public void connectionFactory_notifyStoppedConnection(Connection connection, boolean expected) {
		long containerId = connection.getWeAreConnectingToId();
		log.info("Removing connection [" + connection + "] for container [" + containerId + "]");
		DefaultContainerProxy container = getContainer(containerId);
		container.setConnection(null);		
	};
	
	public void setPipelineConnectionFactory(PipelineServerConnectionFactory<?> pipelineConnectionFactory) {
		pipelineConnectionFactory.register(this);
	}

	@Override
	public DefaultContainerProxy getContainer(long containerId) {
		DefaultContainerProxy containerProxy = connections.get(containerId);
		if (containerProxy == null) {
			DefaultContainerProxy newContainerProxy = new DefaultContainerProxy(containerId);
			containerProxy = connections.putIfAbsent(containerId, newContainerProxy);
			if (containerProxy == null) {
				containerProxy = newContainerProxy;
			}			
		}
		return containerProxy;
	}
}
