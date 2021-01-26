package com.concurrentperformance.pebble.hub.service;

import java.util.Set;

import com.concurrentperformance.pebble.hub.beans.ContainerProxy;
import com.concurrentperformance.pebble.msgcommon.event.AdvertiseProducerAvailability;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.RegisterInterestInEvent;

public interface EventLocationService {

	void registerEventProducer(AdvertiseProducerAvailability event);
	void registerEventConsumer(RegisterInterestInEvent registerInterest);
	
	Set<ContainerProxy> getConsumers(Event event);
	boolean hasInterestedConsumersOtherThanProducer(AdvertiseProducerAvailability event);

}
