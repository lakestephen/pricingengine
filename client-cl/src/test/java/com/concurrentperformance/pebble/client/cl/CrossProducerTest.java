package com.concurrentperformance.pebble.client.cl;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.concurrentperformance.pebble.clientapi.controller.graph.ClientGraphServiceC2S;
import com.concurrentperformance.pebble.clientapi.controller.topology.ClientTopologyServiceC2S;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;
import com.concurrentperformance.pebble.controllerclient.api.exception.ClientException;
import com.concurrentperformance.pebble.msgcommon.graph.stockitems.basic.IntAverage;
import com.concurrentperformance.pebble.msgcommon.graph.stockitems.test.RandomIntegerProducer;

@RunWith(SpringJUnit4ClassRunner.class )
@ContextConfiguration(locations={"/spring/applicationContext-client-cl.xml"})
public class CrossProducerTest {

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

		
		graphService.addGraphItem(mountId1, MOUNT_PATH_1 + ".producer.ask", RandomIntegerProducer.class, 
				null, "event.ask");
		graphService.addGraphItem(mountId2, MOUNT_PATH_2 + ".producer.bid", RandomIntegerProducer.class, 
				null, "event.bid");
		graphService.addGraphItem(mountId1, MOUNT_PATH_1 + ".mid", IntAverage.class, 
				Arrays.asList("event.bid", "event.ask"), "event.mid1");
		graphService.addGraphItem(mountId2, MOUNT_PATH_2 + ".mid", IntAverage.class, 
				Arrays.asList("event.bid", "event.ask"), "event.mid2");
	
		connectionToController.stop();
	}
	
}
