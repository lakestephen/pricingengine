package com.concurrentperformance.pebble.hub.beans.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineConnection;
import com.concurrentperformance.pebble.hub.beans.ContainerProxy;
import com.concurrentperformance.pebble.msgcommon.event.Event;

public class DefaultContainerProxy implements ContainerProxy {

	private final Log log = LogFactory.getLog(this.getClass());

	private final long containerId;
	private final int hash;
	private volatile PipelineConnection connection;
	
	public DefaultContainerProxy(long containerId) {
		this.containerId = containerId;
		this.hash = calculateHashCode(containerId);
	}
	
	@Override
	public void sendEvent(Event event) {
		PipelineConnection localConnection = connection;
		
		if (localConnection == null) {
			log.error("Connection for [" + containerId + "] null for [" + event + "]");
			return;
		}
		
		if (log.isTraceEnabled()) { 
			log.trace("Write to wire [" + event + "], [" + localConnection + "]"+ System.nanoTime()/1000);
		}

		event.write(localConnection.getWriter());
	}

	@Override
	public long getContainerId() {
		return containerId;
	}

	public void setConnection(PipelineConnection connection) {
		this.connection = connection;
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	private static int calculateHashCode(long containerId) {
		return Long.valueOf(containerId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultContainerProxy other = (DefaultContainerProxy) obj;
		if (containerId != other.containerId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Container [" + containerId + "]";
	}
	
}
