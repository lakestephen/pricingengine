package com.concurrentperformance.pebble.hub.beans;

import com.concurrentperformance.pebble.msgcommon.event.Event;

public interface ContainerProxy {

	public void sendEvent(Event event);

	public long getContainerId();	
}
