package com.concurrentperformance.pebble.hub.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.hub.beans.ContainerProxy;
import com.concurrentperformance.pebble.hub.service.ContainerConnectionService;
import com.concurrentperformance.pebble.hub.service.EventLocationService;
import com.concurrentperformance.pebble.msgcommon.event.AdvertiseProducerAvailability;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.RegisterInterestInEvent;
import com.concurrentperformance.pebble.msgcommon.event.StartSendingEvent;

public class DefaultEventLocationService implements EventLocationService {

	private final Log log = LogFactory.getLog(DefaultEventLocationService.class);
	
	private ContainerConnectionService containerConnectionService;
	
	//TODO IMPORTANT: Make sure that this is ONLY EVER ACCEDDED BY THE DISRUPTOR otherwise will need synchronisation!
	
	private Map<String, ContainerProducer> containerProducingEvent = new HashMap<String, ContainerProducer>(); 
	private Map<String, HashSet<ContainerProxy>> eventIdToConsumerContainerId = new HashMap<String, HashSet<ContainerProxy>>();
	
	@Override
	public void registerEventProducer(AdvertiseProducerAvailability producerAvailability) {
		String eventId = producerAvailability.getId();
		long containerId = producerAvailability.getContainerId();

		log.info("Container [" + containerId + "] is a producer for [" + eventId + "]");
		
		ContainerProxy producerContainerProxy = containerConnectionService.getContainer(containerId);
		ContainerProducer containerProducer = new ContainerProducer(producerContainerProxy);
		containerProducingEvent.put(eventId, containerProducer);
//		log.info(this);
		
		// see if we already have consumers who are interested
		boolean interestedConsumers = hasInterestedConsumersOtherThanProducer(producerAvailability);

		// if so, then request they start sending updates for the event.
		if (interestedConsumers) {
			startSendingEvent(eventId, containerProducer);
			containerProducer.setSendingEvent(true);
		}
		else {
			log.info("No consumer yet for [" + eventId + "]");
		}
	}
	
	@Override
	public void registerEventConsumer(RegisterInterestInEvent registerInterest) {
		
		String eventId = registerInterest.getId();
		long containerId = registerInterest.getContainerId();
		
		log.info("Container [" + containerId + "] is a consumer of [" + eventId + "]");

		HashSet<ContainerProxy> containers = eventIdToConsumerContainerId.get(eventId);
		if (containers == null) {
			containers = new HashSet<ContainerProxy>();
			eventIdToConsumerContainerId.put(eventId, containers);
		}
		
		ContainerProxy containerProxy = containerConnectionService.getContainer(containerId);
		containers.add(containerProxy);
		
//		log.info(this);
		
		//check to see if there is a producer and if we already asked for this event to be sent.
		ContainerProducer containerProducer = containerProducingEvent.get(eventId);
		if (containerProducer != null) {
			startSendingEvent(eventId, containerProducer);
		}
		else {
			log.info("No producer yet for [" + eventId + "]");
		}
	}

	private void startSendingEvent(String eventId, ContainerProducer containerProducer) {
		if (!containerProducer.isSendingEvent()) {
			ContainerProxy containerProxy = containerProducer.getContainerProxy();
			log.info("Request container [" + containerProxy.getContainerId() + "] sends event [" + eventId + "]" );
			StartSendingEvent startSendingEvent = new StartSendingEvent(eventId);		
			containerProxy.sendEvent(startSendingEvent);
			containerProducer.setSendingEvent(true);
		}
	}

	@Override
	public Set<ContainerProxy> getConsumers(Event event) {
		Set<ContainerProxy> containers = eventIdToConsumerContainerId.get(event.getId());
		return containers;
	}
	
	@Override
	public boolean hasInterestedConsumersOtherThanProducer(AdvertiseProducerAvailability event) {
		Set<ContainerProxy> containers = getConsumers(event);
		long producerContainerId = event.getContainerId();
		//check if any container other than the producer has an interest.
		boolean otherInterestedConsumers =  (containers !=  null) &&
				(containers.size() > 0 || !containers.contains(producerContainerId)); //TODO Hmmm, this does not look so good, but the hash is a Long, and might be quicker. Premature Optimisation??
		
		return otherInterestedConsumers;
	}
	
	public void setContainerConnectionService(ContainerConnectionService containerConnectionService) {
		this.containerConnectionService = containerConnectionService;
	}

	@Override
	public String toString() {
		return "DefaultProducerLocationService [" +
				"producers=" + containerProducingEvent + 
				", consumers=" + eventIdToConsumerContainerId + "]";
	}
	
	
	class ContainerProducer {
		
		private final ContainerProxy containerProxy;
		private boolean sendingEvent = false; //TODO what do we do when the container is replaced?
		
		ContainerProducer(ContainerProxy containerProxy) {
			this.containerProxy = containerProxy;
		}

		public ContainerProxy getContainerProxy() {
			return containerProxy;
		}
		
		public boolean isSendingEvent() {
			return sendingEvent;
		}

		public void setSendingEvent(boolean isSendingEvent) {
			this.sendingEvent = isSendingEvent;
		}

		@Override
		public String toString() {
			return containerProxy.toString();
		}
				
	}
}
