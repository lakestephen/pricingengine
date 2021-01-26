package com.concurrentperformance.pebble.container.controller;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.controllercontainer.api.graph.ContainerGraphServiceC2SApi;
import com.concurrentperformance.pebble.controllercontainer.api.graph.ContainerGraphServiceS2CApi;

public interface ContainerGraphServiceC2S extends ContainerGraphServiceC2SApi,
        RpcIncommingServiceSupport<ContainerGraphServiceS2CApi> {

}
