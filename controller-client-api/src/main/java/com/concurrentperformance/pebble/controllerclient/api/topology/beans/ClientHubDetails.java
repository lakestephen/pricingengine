package com.concurrentperformance.pebble.controllerclient.api.topology.beans;

public class ClientHubDetails extends ClientTopologyDetails {

	private long listenPort;
	
	public ClientHubDetails(long id, long parentId) {
		super(id, parentId);
	}

	public long getListenPort() {
		return listenPort;
	}

	public void setListenPort(long listenPort) {
		this.listenPort = listenPort;
	}
}
