package com.concurrentperformance.pebble.controllerclient.api.topology.beans;

import java.io.Serializable;


public abstract class ClientTopologyDetails implements Serializable {

	private final long id;
	private final long parentId;
	private String name;
	private ClientTopologyState clientTopologyState = ClientTopologyState.STOPPED;
	
	ClientTopologyDetails(long id, long parentId) {
		this.id = id;
		this.parentId = parentId;
	}
	
	public long getId() {
		return id;
	}
	
	public long getParentId() {
		return parentId;
	}
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public ClientTopologyState getClientTopologyState() {
		return clientTopologyState;
	}

	public void setClientTopologyState(ClientTopologyState clientTopologyState) {
		this.clientTopologyState = clientTopologyState;
	}

	@Override
	public String toString() {
		return "ClientTopologyDetails [id=" + id + ", parentId=" + parentId
				+ ", name=" + name + "]";
	}	
	
}