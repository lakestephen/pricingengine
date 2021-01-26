package com.concurrentperformance.pebble.util.thread;

import java.net.Socket;

public class ThreadNamer {

	
	public static String getThreadName(Socket socket) {
		if (socket == null) {
			throw new NullPointerException("socket must not be null");
		}
		String name = socket.getInetAddress() + ":" + socket.getPort() + ">" + socket.getLocalPort();
		return name;
	}
	
}
