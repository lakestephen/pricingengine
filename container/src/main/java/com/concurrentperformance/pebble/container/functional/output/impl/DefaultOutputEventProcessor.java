package com.concurrentperformance.pebble.container.functional.output.impl;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineConnection;
import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineWriter;
import com.concurrentperformance.pebble.container.controller.ContainerMonitorEventServiceC2S;
import com.concurrentperformance.pebble.container.functional.output.OutputEventProcessor;
import com.concurrentperformance.pebble.msgcommon.event.ControlEvent;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultOutputEventProcessor implements OutputEventProcessor { //TODO do we need this class?

    private final Log log = LogFactory.getLog(this.getClass());

    private PipelineWriter hubPipelineWriter;
    private ContainerMonitorEventServiceC2S monitorEventService;

    @Override
    public void outputEvent(Event event, boolean hubInterest, boolean monitorInterest) {
        log.info("Process Event [" + event + "], hubInterest [" + hubInterest + "], monitorInterest[" + monitorInterest + "]");
        if (hubInterest) {
            writeToHub(event);
        }

        if (monitorInterest) {
            writeToMonitor(event);
        }
    }

    private void writeToMonitor(Event event) {
        monitorEventService.monitorEvent_updateEventNotification(event); //TODO need to filter what goes back to stuff that is registered for notification.
    }

    @Override
    public void processControlEvent(ControlEvent event) {
        writeToHub(event);
    }

    private void writeToHub(Event event) {
        if (log.isTraceEnabled()) {
            log.trace("Write to wire [" + event + "]" + System.nanoTime() / 1000);
        }
        event.write(hubPipelineWriter);
    }


    public void setHubPipelineConnection(PipelineConnection hubPipelineConnection) {
        this.hubPipelineWriter = hubPipelineConnection.getWriter();
    }

    public void setMonitorEventService(ContainerMonitorEventServiceC2S monitorEventService) {
        this.monitorEventService = monitorEventService;
    }

}
