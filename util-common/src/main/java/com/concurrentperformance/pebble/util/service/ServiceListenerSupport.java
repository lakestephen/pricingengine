package com.concurrentperformance.pebble.util.service;


public interface ServiceListenerSupport<IL> {

	public void register(IL listener);

	public void deregister(IL listener);
}
