package com.concurrentperformance.pebble.ui.functional.topology.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyDetails;
import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyState;

public abstract class SkelitalTopologyNode implements TopologyNode {

	private ClientTopologyDetails clientTopologyDetails;
	private final TopologyNode parent;
	private final List<TopologyNode> children = new ArrayList<TopologyNode>();

	
	public SkelitalTopologyNode(TopologyNode parent, ClientTopologyDetails childDetails) {
		this.parent = parent;
		this.clientTopologyDetails = childDetails;
	}
	
	@Override
	public long getId() {
		return clientTopologyDetails.getId();
	}
	
	@Override
	public String getName() {
		return clientTopologyDetails.getName();
	}

	@Override
	public void setClientTopologyDetails(ClientTopologyDetails clientTopologyDetails) {
		checkType(clientTopologyDetails);
		this.clientTopologyDetails = clientTopologyDetails;
		if (getParent() != null) {
			getParent().sortChildren();
		}
	}
	
	abstract void checkType(ClientTopologyDetails clientTopologyDetails);

	@Override
	public ClientTopologyState getClientTopologyState() {
		return clientTopologyDetails.getClientTopologyState();
	}
	
	@Override
	public TopologyNode getParent() {
		return parent;
	}
	
	@Override
	public TopologyNode getChild(long id) {
		for (TopologyNode child : children){
			if (id == child.getId()) {
				return child;
			}
		}
		return null;
	}
	
	@Override
	public TopologyNode getChild(int index) {
		return children.get(index);
	}
	 
	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public TopologyNode addChild(ClientTopologyDetails childDetails) {
		// check that we do not already have one.
		TopologyNode existingNode = getChild(childDetails.getId());
		if (existingNode != null) {
			throw new IllegalArgumentException("Already have a child [" + childDetails + "]");
		}

		TopologyNode child = buildChild(childDetails);
		children.add(child);
		return child;
	}

	protected abstract TopologyNode buildChild(ClientTopologyDetails childDetails);

	@Override
	public TopologyNode removeChild(long childId) {
		TopologyNode child = getChild(childId);
		if (child != null) {
			children.remove(child);
		}
		return child;
	}

	@Override
	public int getChildIndex(long id) {
		TopologyNode child = getChild(id);
		return getChildIndex(child);
	}
	
	@Override
	public int getChildIndex(TopologyNode child) {
		int index = -1;
		if (child != null) {
			index = children.indexOf(child);
		}
		return index;
	}
	
	@Override
	public void sortChildren() {
		Collections.sort(children, TopologyNode.BY_NAME);		
	}


	@Override
	public void setClientTopologyState(ClientTopologyState clientTopologyState) {
		clientTopologyDetails.setClientTopologyState(clientTopologyState);		
	}

	public ClientTopologyDetails getClientTopologyDetails() {
		return clientTopologyDetails;
	}
	
	@Override
	public void removeAllChildren() {
		children.clear();		
	}

	@Override
	public String toString() {
		return getName();
	}
}