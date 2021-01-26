package com.concurrentperformance.pebble.container.functional.output;

import com.concurrentperformance.pebble.msgcommon.event.ControlEvent;
import com.concurrentperformance.pebble.msgcommon.event.Event;


public interface OutputEventProcessor {

    public void outputEvent(Event event, boolean hubInterest, boolean monitorInterest);

    /**
     * Only gets sent to the hub, and not to interested clients.
     *
     * @param event
     */
    public void processControlEvent(ControlEvent event);
}
