package com.concurrentperformance.pebble.controllercontainer.api.graph;

import java.util.List;

import com.concurrentperformance.pebble.controllercontainer.api.graph.exception.ContainerGraphServiceException;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculation;

public interface ContainerGraphServiceS2CApi {

	long addGraphItem(long mountId, String graphPath,
			Class<? extends GraphCalculation> graphItem,
			List<String> inputEventIds, String outputEventId) throws ContainerGraphServiceException;

	void populateAllGraphItems(long mountId);

	void setGraphValue(long id, int value) throws ContainerGraphServiceException;

	void setPersisted(long id, boolean persisted) throws ContainerGraphServiceException;
}
