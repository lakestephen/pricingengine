package com.concurrentperformance.pebble.util.service;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

//TODO with all notifications, need t find a way of not bombing out when one throws an exception - AOP ???
public class DefaultServiceListenerSupport<IL> implements ServiceListenerSupport<IL> {

	private Set<IL> listeners = new CopyOnWriteArraySet<IL>(); 

	@Override
	public void register(IL listener) { 
		listeners.add(listener);		
	}

	@Override
	public void deregister(IL listener) {
		listeners.remove(listener);		
	}

	public Set<IL> getListeners() {
		return listeners;
	}
	
}
