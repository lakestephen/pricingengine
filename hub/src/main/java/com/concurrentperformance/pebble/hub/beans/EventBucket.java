package com.concurrentperformance.pebble.hub.beans;

import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.lmax.disruptor.EventFactory;

public class EventBucket {

	private Event event;
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public final static EventFactory<EventBucket> EVENT_BUCKET_FACTORY = new EventFactory<EventBucket>()
    {
        public EventBucket newInstance()
        {
            return new EventBucket();
        }
    };
}
