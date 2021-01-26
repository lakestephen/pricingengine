package com.concurrentperformance.pebble.controller.functional.topology.service.monitor;

import java.util.concurrent.atomic.AtomicLong;

public class SpawnedComponentInstanceIdFactory {

	private static AtomicLong count = new AtomicLong(0);
	
	static long getNextId() {
		long id = count.incrementAndGet();
		return id;
	}
}
