package com.concurrentperformance.pebble.container.functional.receive.service;

import com.concurrentperformance.pebble.container.functional.items.beans.ContainerItem;
import com.concurrentperformance.pebble.container.functional.items.service.ContainerItemsService;
import com.concurrentperformance.pebble.msgcommon.event.ControlEvent;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.StartSendingEvent;
import com.lmax.disruptor.EventHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

/**
 * Responsible for handling the Events from the disrupter, finding the
 * consumers for the event, and then telling the next handler
 * that the items are dirty and need recalculating.
 *
 * @author Stephen
 */
public class DistributeToConsumersHandler implements EventHandler<EventBucket> {

    private final Log log = LogFactory.getLog(this.getClass());

    private ContainerItemsService containerItemsService;

    @Override
    public void onEvent(final EventBucket eventBucket, final long sequence,
                        final boolean endOfBatch) throws Exception {
        log.info("Processing event [" + eventBucket.getEvent() + "] sequence [" + sequence + "]");
        Event event = eventBucket.getEvent();

        if (event instanceof ControlEvent) {
            handleControlEvent((ControlEvent) event);
        } else {
            handleDistributeEvent(eventBucket);
        }
    }

    private void handleControlEvent(ControlEvent event) {
        if (event instanceof StartSendingEvent) {
            log.info("Hub has requested we start sending [" + event.getId() + "]");

            ContainerItem producer = containerItemsService.findProducerItem(event);
            producer.setHubInterest(true);
        }
    }

    private void handleDistributeEvent(final EventBucket eventBucket) {
        final Event event = eventBucket.getEvent();

        final Set<ContainerItem> internalConsumers = containerItemsService.findConsumerItems(event);

        for (ContainerItem containerItem : internalConsumers) {
            containerItem.updateInputEvent(event);
        }

        // Set the dirty container items for the next consumer to re-calculate
        log.info("Setting dirty container items [" + internalConsumers + "]");
        eventBucket.setDirtyContainerItems(internalConsumers);
    }


    public void setContainerItemsService(ContainerItemsService containerItemsService) {
        this.containerItemsService = containerItemsService;
    }
}
