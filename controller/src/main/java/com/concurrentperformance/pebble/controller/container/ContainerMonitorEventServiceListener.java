package com.concurrentperformance.pebble.controller.container;

import com.concurrentperformance.pebble.msgcommon.event.Event;

public interface ContainerMonitorEventServiceListener {
	
	void monitorEvent_updateEventNotification(Event event);

}
