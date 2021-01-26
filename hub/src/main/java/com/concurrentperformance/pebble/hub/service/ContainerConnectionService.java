package com.concurrentperformance.pebble.hub.service;

import com.concurrentperformance.pebble.hub.beans.ContainerProxy;

public interface ContainerConnectionService {

	ContainerProxy getContainer(long containerId);

}
