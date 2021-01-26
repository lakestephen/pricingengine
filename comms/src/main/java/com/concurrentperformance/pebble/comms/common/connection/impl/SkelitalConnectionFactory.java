package com.concurrentperformance.pebble.comms.common.connection.impl;

import java.net.Socket;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.common.connection.Connection;
import com.concurrentperformance.pebble.comms.common.connection.ConnectionListener;
import com.concurrentperformance.pebble.comms.common.server.socketlistener.ConnectionFactoryListener;
import com.concurrentperformance.pebble.util.concurrent.ConcurrentHashSet;

public abstract class SkelitalConnectionFactory //TODO this should be in a common area (client and server)
	implements ConnectionListener { 

	private Set<ConnectionFactoryListener> listeners = new ConcurrentHashSet<ConnectionFactoryListener>();

	private final Log log = LogFactory.getLog(this.getClass());

	protected String weAreA; 
	protected long weAreAId; 
	
	protected abstract Connection buildConnection(Socket client);

	@Override
	public void connection_notifyStarted(Connection connection) {
		for(ConnectionFactoryListener listener : listeners ) {
			listener.connectionFactory_notifyNewConnection(connection);
		}
	}

	@Override
	public void connection_notifyStopped(Connection connection, boolean expected) {
		for(ConnectionFactoryListener listener : listeners ) {
			listener.connectionFactory_notifyStoppedConnection(connection, expected);
		}
	}
	
	public void register(ConnectionFactoryListener listener) {
		listeners.add(listener);
	}

	public void deregister(ConnectionFactoryListener listener) {
		listeners.remove(listener);		
	}
	
	public void setWeAreA(String weAreA) {
		this.weAreA = weAreA;
	}
	
	public void setWeAreAId(long weAreAId) {
		this.weAreAId = weAreAId;
	}
}
