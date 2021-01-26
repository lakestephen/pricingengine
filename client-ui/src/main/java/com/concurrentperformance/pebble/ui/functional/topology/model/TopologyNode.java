package com.concurrentperformance.pebble.ui.functional.topology.model;

import java.util.Comparator;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyState;
import com.concurrentperformance.pebble.ui.common.tree.TreeNodeSupport;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public interface TopologyNode extends TreeNodeSupport<TopologyNode> {
	
	public static Comparator<TopologyNode> BY_NAME = new Comparator<TopologyNode>() {
		@Override
		public int compare(TopologyNode tn1, TopologyNode tn2) {
			return tn1.getName().compareTo(tn2.getName());
		}
	};
	
	TopologyNode addChild(ClientTopologyDetails childDetails);
	TopologyNode removeChild(long childId);
	int getChildIndex(long id); 
	TopologyNode getChild(long	 id); // TODO rename getChildById - is used?
	TopologyNode getChild(int index); // TODO rename getChildByIndex - is used?
	int getChildCount();

	
	long getId();
	ClientTopologyState getClientTopologyState();
	void setClientTopologyState(ClientTopologyState clientTopologyState);
	void sortChildren(); //TODO should this be pushed down to TreeNodeSupport
	void setClientTopologyDetails(ClientTopologyDetails childDetails);

}
