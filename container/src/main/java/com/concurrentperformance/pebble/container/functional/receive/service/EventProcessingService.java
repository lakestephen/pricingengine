package com.concurrentperformance.pebble.container.functional.receive.service;

import com.concurrentperformance.pebble.comms.pipeline.connection.PipelineReceiver;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.util.thread.ThreadFactoryBuilder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class is responsible for taking receiving the events from the
 * pipeline, and getting them into the disrupter ring buffer
 *
 * @author Stephen
 */
public class EventProcessingService implements PipelineReceiver<Event> {

    private final Log log = LogFactory.getLog(this.getClass());

    private List<EventHandler<EventBucket>> handlers;

    private static final int RING_SIZE = 1024; //TODO Size properly
    private final Executor EXECUTOR = Executors.newFixedThreadPool(2, ThreadFactoryBuilder.BuildThreadFactory("ProcessEvents_"));

    private final Disruptor<EventBucket> disruptor =
            new Disruptor<EventBucket>(EventBucket.EVENT_BUCKET_FACTORY,
                    EXECUTOR,
                    new SingleThreadedClaimStrategy(RING_SIZE),
                    new BlockingWaitStrategy());

    private SimpleEventTranslator eventTranslator = new SimpleEventTranslator(); //TODO is this thread safe?

    public void start() {
        EventHandlerGroup lastGroup = null;

        for (EventHandler<EventBucket> handler : handlers) {
            log.info("Handle events with [" + handler + "]");
            if (lastGroup == null) {
                lastGroup = disruptor.handleEventsWith(handler);
            } else {
                lastGroup.then(handler);
            }
        }

        disruptor.start();
    }

    @Override
    public void receive(Event event) {
        eventTranslator.setEvent(event);
        disruptor.publishEvent(eventTranslator);
    }

    public void setHandlers(List<EventHandler<EventBucket>> handlers) {
        this.handlers = handlers;
    }

    private class SimpleEventTranslator implements EventTranslator<EventBucket> {

        private Event event;

        public void setEvent(Event event) {
            this.event = event;
        }

        ;

        @Override
        public EventBucket translateTo(EventBucket bucket, long sequence) {
            bucket.setEvent(event);
            return bucket;
        }
    }
}
