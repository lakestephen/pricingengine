package com.concurrentperformance.pebble.container.controller;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.controllercontainer.api.monitorevent.ContainerMonitorEventServiceC2SApi;
import com.concurrentperformance.pebble.controllercontainer.api.monitorevent.ContainerMonitorEventServiceS2CApi;

public interface ContainerMonitorEventServiceC2S extends ContainerMonitorEventServiceC2SApi,
        RpcIncommingServiceSupport<ContainerMonitorEventServiceS2CApi> {

}
