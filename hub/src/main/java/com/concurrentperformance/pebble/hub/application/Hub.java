package com.concurrentperformance.pebble.hub.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.concurrentperformance.pebble.comms.pipeline.server.socketlistener.PipelineServerConnectionFactory;
import com.concurrentperformance.pebble.msgcommon.event.Event;

public class Hub {

    private final Log log = LogFactory.getLog(this.getClass());

    private PipelineServerConnectionFactory<Event> listenerForContainers;

    public void start() {
        listenerForContainers.start();
    }

    public void setListenerForContainers(
            PipelineServerConnectionFactory<Event> listenerForContainers) {
        this.listenerForContainers = listenerForContainers;
    }
}