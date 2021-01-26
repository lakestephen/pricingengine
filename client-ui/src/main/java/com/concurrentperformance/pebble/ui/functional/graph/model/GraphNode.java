package com.concurrentperformance.pebble.ui.functional.graph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.controllerclient.api.topology.beans.ClientTopologyState;
import com.concurrentperformance.pebble.ui.common.tree.TreeNodeSupport;


public class GraphNode implements TreeNodeSupport<GraphNode> {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	enum GraphNodeType {
		UNBOUND, // This is just for imposing the tree structure. i.e. for input.bond.value, this could be the 'input' or the 'bond'
		CALCULATION, 
		MOUNT,
	}
	
	public static Comparator<GraphNode> BY_NAME = new Comparator<GraphNode>() {
		@Override
		public int compare(GraphNode gn1, GraphNode gn2) {
			return gn1.getName().compareTo(gn2.getName());
		}
	};

	private final GraphNode parent;
	private String name;

	// graph item specific values
	private long graphId;  //TODO where to keep this data that is specific to only some nodes?
	private int value;//TODO where to keep this data that is specific to only some nodes?
	private String outputEventId;//TODO where to keep this data that is specific to only some nodes?
	
	// mount item specific values. 
	private long mountId;  //TODO where to keep this data that is specific to only some nodes?
	private long mountContainerId;  //TODO where to keep this data that is specific to only some nodes?

	private GraphNodeType type = GraphNodeType.UNBOUND;
	private ClientTopologyState clientTopologyState = ClientTopologyState.STOPPED;
	private final List<GraphNode> children = new ArrayList<GraphNode>();

	
	public GraphNode(GraphNode parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		throw new UnsupportedOperationException();
	}
	
	public ClientTopologyState getClientTopologyState() {
		return clientTopologyState;
	}
	
	public void setClientTopologyState(ClientTopologyState clientTopologyState) {
		this.clientTopologyState = clientTopologyState;
	}		
	
	public GraphNode getParent() {
		return parent;
	}
	
	public GraphNode getChild(String name) {
		for (GraphNode child : children){
			if (name.equals(child.getName())) {
				return child;
			}
		}
		return null;
	}
	
	public GraphNode getChild(int index) {
		return children.get(index);
	}
	 
	public int getChildCount() {
		return children.size();
	}

	void setCalculationType(long graphId) {
		this.graphId = graphId;
		this.type = GraphNodeType.CALCULATION;		
	}
	

	void setMountType(long mountId) {
		this.mountId = mountId;
		this.type = GraphNodeType.MOUNT;		
	}

	public void setMountContainer(long containerId) {
		if (!isMountType()) {
			log.warn("Cant set mount container for [" + this + "] as not mount type");
		}
		this.mountContainerId = containerId;
	}

	public void setCalculationValue(int value) {
		this.value = value;
	}
	
	public void setCalculationOutputEventId(String outputEventId) {
		this.outputEventId = outputEventId;		
	}

	public boolean isCalculationType() {
		return GraphNodeType.CALCULATION.equals(type);	 
	}

	public boolean isMountType() {
		return GraphNodeType.MOUNT.equals(type);	 
	}

	public boolean isMountHostedInContainer() {
		return mountContainerId > 0;
	}

	public long getMountId() {
		return mountId;
	}

	public GraphNode addChild(String childName) {
		// check that we do not already have one.
		GraphNode existingNode = getChild(childName);
		if (existingNode == null) {
			GraphNode child = new GraphNode(this, childName);
			children.add(child);
			Collections.sort(children,GraphNode.BY_NAME);
			return child;
		}
		return null;
	}

	public GraphNode removeChild(String childName) { //TODO push down into TreeNodeSupport, with generic support for id as string or Long
		GraphNode child = getChild(childName);
		if (child != null) {
			children.remove(child);
		}
		return child;
	}

	public int getChildIndex(String childName) {
		GraphNode child = getChild(childName);
		return getChildIndex(child);
	}
	
	public int getChildIndex(GraphNode child) {
		int index = -1;
		if (child != null) {
			index = children.indexOf(child);
		}
		return index;
	}

	public void removeAllChildren() {
		children.clear();		
	}
	
	@Override
	public String toString() {
		switch (type) {
		case CALCULATION:
			return getName() + ":" + value;
		default:
			return getName();
		}
	}


}
