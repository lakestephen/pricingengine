package com.concurrentperformance.pebble.container.functional.receive.service;

import com.concurrentperformance.pebble.container.functional.items.beans.ContainerItem;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.lmax.disruptor.EventFactory;

import java.util.Set;

public class EventBucket {

    private Event event;
    private Set<ContainerItem> dirtyContainerItems;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Set<ContainerItem> getDirtyContainerItems() {
        return dirtyContainerItems;
    }

    public void setDirtyContainerItems(Set<ContainerItem> containerItem) {
        this.dirtyContainerItems = containerItem;
    }

    public final static EventFactory<EventBucket> EVENT_BUCKET_FACTORY = new EventFactory<EventBucket>() {
        public EventBucket newInstance() {
            return new EventBucket();
        }
    };
}
