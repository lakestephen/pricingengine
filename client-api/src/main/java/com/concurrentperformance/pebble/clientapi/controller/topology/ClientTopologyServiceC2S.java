package com.concurrentperformance.pebble.clientapi.controller.topology; //TODO this should be in a client module wiith a calculation.client package?

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceC2SApi;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceS2CApi;

public interface ClientTopologyServiceC2S extends ClientTopologyServiceC2SApi,
        RpcIncommingServiceSupport<ClientTopologyServiceS2CApi> {


}
