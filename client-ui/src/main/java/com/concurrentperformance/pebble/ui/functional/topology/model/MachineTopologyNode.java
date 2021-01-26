package com.concurrentperformance.pebble.ui.functional.topology.model;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientHubDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientMachineDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;




/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
//TODO synchronise
public class MachineTopologyNode extends SkelitalTopologyNode implements TopologyNode {

	MachineTopologyNode(ApplicationTopologyNode parent, ClientMachineDetails clientMachineDetails) {
		super(parent, clientMachineDetails);
	}
	
	@Override
	protected TopologyNode buildChild(ClientTopologyDetails clientTopologyDetails) {
		return new HubTopologyNode(this, (ClientHubDetails)clientTopologyDetails);
	}
	
	@Override
	void checkType(ClientTopologyDetails clientTopologyDetails) {
		if (!(clientTopologyDetails instanceof ClientMachineDetails)) {
			throw new IllegalArgumentException("childDetails [" + clientTopologyDetails + "] is not an instance of ClientMachineDetails");
		}
	}
}