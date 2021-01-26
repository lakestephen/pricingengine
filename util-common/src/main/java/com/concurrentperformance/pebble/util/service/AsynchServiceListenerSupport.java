package com.concurrentperformance.pebble.util.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.concurrentperformance.pebble.util.thread.ThreadFactoryBuilder;

//TODO with all notifications, need t find a way of not bombing out when one throws an exception - AOP ???
public class AsynchServiceListenerSupport<IL> extends DefaultServiceListenerSupport<IL> {

	private Executor executor = Executors.newSingleThreadExecutor(
			ThreadFactoryBuilder.BuildThreadFactory(this.getClass().getSimpleName() +"_Notify"));

	
	public void submitTask(Runnable task) {
		executor.execute(task);		
	}
}
