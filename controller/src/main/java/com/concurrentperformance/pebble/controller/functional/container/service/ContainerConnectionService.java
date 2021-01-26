package com.concurrentperformance.pebble.controller.functional.container.service;

import com.concurrentperformance.pebble.controller.functional.container.exception.ContainerConnectionException;
import com.concurrentperformance.pebble.controller.functional.topology.beans.MountDescriptor;
import com.concurrentperformance.pebble.controllercontainer.api.graph.ContainerGraphServiceS2CApi;

public interface ContainerConnectionService {

	public ContainerGraphServiceS2CApi getGraphServiceForMount(MountDescriptor mount) throws ContainerConnectionException ;
	
}
