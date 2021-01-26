package com.concurrentperformance.pebble.msgcommon.graph.stockitems.test;

import com.concurrentperformance.pebble.msgcommon.event.IntEvent;
import com.concurrentperformance.pebble.msgcommon.graph.GraphProducer;
import com.concurrentperformance.pebble.msgcommon.graph.support.SkelitalGraphProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class RandomIntegerProducer extends SkelitalGraphProducer implements GraphProducer { //TODO remove this. 

	private final Log log = LogFactory.getLog(this.getClass());

	private static Class<?>outputEventType = Integer.class;
	Random generator = new Random();
	
	static volatile int count = 0;
	
	public void start() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
//				long sleepTime = 3000 +(++count * 2000); //only any good when they are in the same container!
//				long sleepTime = 30000 ;
				long sleepTime = 1000 + (long)(Math.random() * 1000 );
				while(true) {
					try {
						Thread.sleep(sleepTime );
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					IntEvent event = new IntEvent(getOutputEventId(), generator.nextInt()%100);
					log.info("RandomIntegerProducer[" + event + "]");
					getOutputEventHandler().acceptOutputEvent(event);
				}
			}
		}, 
		"Producer:" + getOutputEventId() ).start();
	}
	
	@Override
	public String getCalculationName() {
		return "Mock Integer Producer";
	}

	@Override
	public String getHelpText() {
		// TODO Auto-generated method stub
		return "TEST ONLY: A mock producer that produces a random number every 5 seconds.";
	}

	@Override
	public Class<?> getOutputEventDefinition() {
		return outputEventType;
	}

}
