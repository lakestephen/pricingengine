package com.concurrentperformance.pebble.container.functional.items.service;

import com.concurrentperformance.pebble.container.functional.items.beans.ContainerItem;
import com.concurrentperformance.pebble.msgcommon.event.Event;

import java.util.Set;

public interface ContainerItemsService {

    void start();

    Set<ContainerItem> findConsumerItems(Event event);

    ContainerItem findProducerItem(Event event);

    /**
     * Set the value from some other source other than internal calculation.
     * i.e. when the user sets teh value, or it is loaded from the database.
     *
     * @param eventId
     * @param value
     */
    void setValueArtificially(String eventId, int value);

}
