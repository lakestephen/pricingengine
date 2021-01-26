package com.concurrentperformance.pebble.comms.common.connection;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;

public interface Connection {

	public void setSocket(Socket socket);
	public void start() throws IOException;
	public void stop();  
	public void stopWithException(Exception cause, String msg, Log otherLogger);	
	public void imminentStop();
	
	public void handshakeRecieved(String weAreConnectingTo, long weAreConnectingToId);

	public boolean isStopped(); 
	public boolean isStoppedOrStopping();

	public String getWeAreA();
	public long getWeAreAId();
	
	public String getWeAreConnectingTo();
	public long getWeAreConnectingToId();

	public void register(ConnectionListener listener); 
	public void deregister(ConnectionListener listener);
	
}
