package com.concurrentperformance.pebble.controller.client;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReceiver;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 *
 * @author: Stephen
 */
public class ClientEventBufferService implements PipelineReceiver<Event> {

    /** This is deliberately a static as we will be creating a lot of these */
    private static final Log log = LogFactory.getLog(Event.class);


    @Override
    public void receive(Event item) {
        log.info("ClientEventBufferService Received [" + item + "]");
    }
}
