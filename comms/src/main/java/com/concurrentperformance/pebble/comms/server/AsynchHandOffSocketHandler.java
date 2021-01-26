package com.concurrentperformance.pebble.comms.server;

import java.net.Socket;

public interface AsynchHandOffSocketHandler { 

	String getServerThreadName();
	void handOffNewSocketConnection(Socket client);
	
}
