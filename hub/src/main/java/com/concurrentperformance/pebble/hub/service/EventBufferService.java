package com.concurrentperformance.pebble.hub.service;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReceiver;
import com.concurrentperformance.pebble.hub.beans.ContainerProxy;
import com.concurrentperformance.pebble.hub.beans.EventBucket;
import com.concurrentperformance.pebble.msgcommon.event.AdvertiseProducerAvailability;
import com.concurrentperformance.pebble.msgcommon.event.ControlEvent;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.RegisterInterestInEvent;
import com.concurrentperformance.pebble.util.thread.ThreadFactoryBuilder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.dsl.Disruptor;


public class EventBufferService implements PipelineReceiver<Event> {

	private final Log log = LogFactory.getLog(this.getClass());

	private EventLocationService eventLocationService;
		
	private static final int RING_SIZE = 1024; 
	private final Executor EXECUTOR = Executors.newSingleThreadExecutor(ThreadFactoryBuilder.BuildThreadFactory("EventBuffer-")); 
	
	private final Disruptor<EventBucket> disruptor =
			  new Disruptor<EventBucket>(EventBucket.EVENT_BUCKET_FACTORY, 
					  					EXECUTOR, 
			                            new SingleThreadedClaimStrategy(RING_SIZE),
			                            new BlockingWaitStrategy());

	EventBufferService() {
		disruptor.handleEventsWith(handler);
		disruptor.start();
	}
	
	private SimpleEventTranslator eventTranslator = new SimpleEventTranslator(); //TODO is this thread safe?
	
	@Override
	public void receive(Event event) {
		eventTranslator.setEvent(event); 
		//TODO this method will be called with many threads. Disruptor can't handle that!!!.  
		disruptor.publishEvent(eventTranslator);
	}

	final EventHandler<EventBucket> handler = new EventHandler<EventBucket>() {
		public void onEvent(final EventBucket eventBucket, final long sequence,
				final boolean endOfBatch) throws Exception {
			
			Event event = eventBucket.getEvent();
			
			if (event instanceof ControlEvent) {
				handleControlEvent((ControlEvent)event);  //TODO can we have a seperate hadler for control events that runs first in the ring buffer?
			}
			else {
				handleDistributeEvent(event);
			}
		}
	};
	
	private void handleControlEvent(ControlEvent event) {
		if (event instanceof AdvertiseProducerAvailability) {
			AdvertiseProducerAvailability producerAvailability = (AdvertiseProducerAvailability)event;
			eventLocationService.registerEventProducer(producerAvailability);
		}
		else if (event instanceof RegisterInterestInEvent) {
			RegisterInterestInEvent registerInterest = (RegisterInterestInEvent)event;
			eventLocationService.registerEventConsumer(registerInterest);
		}
	}
	
	private void handleDistributeEvent(Event event) {
		Set<ContainerProxy> consumers = eventLocationService.getConsumers(event);

//		if (log.isTraceEnabled()){
			log.info("Distribute [" + event + "] to containers [" + consumers + "]" );
	//	}

		
		//TODO would be better to have a proxy for each container that has a connection. Would save a lookup on the fastpath.
		if (consumers == null || consumers.isEmpty()) {
			// TODO We should not have messages that have no consumers.
			log.warn("No consumers for [" + event + "]");
			return;
		}
		
		for(ContainerProxy container : consumers) { 
			container.sendEvent(event);
		}
	} 
	
	private class SimpleEventTranslator implements EventTranslator<EventBucket> {

		private Event event;
		
		public void setEvent(Event event) {
			this.event = event;
		};
		
		@Override
		public EventBucket translateTo(EventBucket bucket, long sequence) {
			bucket.setEvent(event);
			return bucket;
		}
	}
	
	public void setProducerLocationService(EventLocationService producerLocationService) {
		this.eventLocationService = producerLocationService; 
	}

}
