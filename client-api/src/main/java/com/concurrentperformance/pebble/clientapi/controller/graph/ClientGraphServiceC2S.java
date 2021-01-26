package com.concurrentperformance.pebble.clientapi.controller.graph;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.controllerclient.api.graph.ClientGraphServiceC2SApi;
import com.concurrentperformance.pebble.controllerclient.api.graph.ClientGraphServiceS2CApi;

public interface ClientGraphServiceC2S extends ClientGraphServiceC2SApi,
        RpcIncommingServiceSupport<ClientGraphServiceS2CApi> {

}
