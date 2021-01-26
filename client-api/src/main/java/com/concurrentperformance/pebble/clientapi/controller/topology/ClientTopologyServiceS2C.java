package com.concurrentperformance.pebble.clientapi.controller.topology;

import com.concurrentperformance.pebble.comms.rpc.client.RpcClientC2SSupport;
import com.concurrentperformance.pebble.controllerclient.api.topology.ClientTopologyServiceS2CApi;

public interface ClientTopologyServiceS2C extends
        ClientTopologyServiceS2CApi, RpcClientC2SSupport {

}
