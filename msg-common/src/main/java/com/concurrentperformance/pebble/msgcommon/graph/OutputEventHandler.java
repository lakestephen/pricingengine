package com.concurrentperformance.pebble.msgcommon.graph;

import com.concurrentperformance.pebble.msgcommon.event.Event;

/**
 * Marker interface for handling the putput of an event.
 * 
 * @author Stephen Lake
 *
 */
public interface OutputEventHandler { //TODO where should this live?
//TODO should this be renamed?
	
	//
	void acceptOutputEvent(Event event);
}
