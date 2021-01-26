package com.concurrentperformance.pebble.controllerclient.api.graph;

import java.util.List;

import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculation;

public interface ClientGraphServiceC2SApi {

	void populateAllMounts();
	void registerForUpdatesFrom(long mountId) throws ClientException ;

	long addGraphItem(long mountId, String graphPath, Class<? extends GraphCalculation> graphItem, List<String> inputEventIds, String outputEventId) throws ClientException;
	
	void removeGraphItem(long id);
	
	void setPersisted(long id, long mountId, boolean persisted) throws ClientException;

	void setGraphValue(long id, long mountId, int value) throws ClientException;

}
