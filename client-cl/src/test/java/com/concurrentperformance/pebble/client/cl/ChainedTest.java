 package com.concurrentperformance.pebble.client.cl;

import com.concurrentperformance.pebble.clientapi.controller.graph.ClientGraphServiceC2S;
import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.msgcommon.graph.stockitems.basic.IntForward;
import com.concurrentperformance.pebble.msgcommon.graph.stockitems.test.RandomIntegerProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class )
@ContextConfiguration(locations={"/spring/applicationContext-client-cl.xml"})
public class ChainedTest {

	@Autowired	
	private ClientTopologyServiceC2S topologyService;

	@Autowired	
	private ClientGraphServiceC2S graphService;
	
	@Autowired	
	private RpcConnection connectionToController;
	
	
	@Test
	public void crossProducer() throws ClientException {
		
		long machineId = topologyService.createMachine("localhost");
		long hubId = topologyService.createHub(machineId, "Hub01");
		topologyService.setHubListenPort(hubId, 5000);
		long contId1 = topologyService.createContainer(hubId, "Cont01");
		long contId2 = topologyService.createContainer(hubId, "Cont02");

		String MOUNT_PATH_1 = "graph";
		long mountId1 = topologyService.createMount(MOUNT_PATH_1);
		topologyService.hostMountInContainer(mountId1, contId1);

		String MOUNT_PATH_2 = "test";
		long mountId2 = topologyService.createMount(MOUNT_PATH_2);
		topologyService.hostMountInContainer(mountId2, contId2);

		
		graphService.addGraphItem(mountId1, MOUNT_PATH_1 + ".valueproducer", RandomIntegerProducer.class, 
				null, "event.value0");

		for (int i=1;i<20;) {
			graphService.addGraphItem(mountId1, MOUNT_PATH_1 + ".value" + i, IntForward.class, 
				Arrays.asList("event.value" + (i-1)), "event.value" + i);
			i++;
			graphService.addGraphItem(mountId2, MOUNT_PATH_2 + ".value" + i, IntForward.class, 
				Arrays.asList("event.value" + (i-1)), "event.value" + i);
			i++;
		}

	
		connectionToController.stop();
	}

	
}
