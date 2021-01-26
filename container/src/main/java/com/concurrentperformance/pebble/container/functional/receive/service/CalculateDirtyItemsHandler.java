package com.concurrentperformance.pebble.container.functional.receive.service;

import com.concurrentperformance.pebble.container.functional.items.beans.ContainerItem;
import com.lmax.disruptor.EventHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

public class CalculateDirtyItemsHandler implements EventHandler<EventBucket> {

    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public void onEvent(final EventBucket eventBucket, final long sequence,
                        final boolean endOfBatch) throws Exception {
        Set<ContainerItem> dirtyContainerItems = eventBucket.getDirtyContainerItems();
        log.info("Getting dirty container items [" + dirtyContainerItems + "], for event [" + eventBucket.getEvent() + "] sequence [" + sequence + "]");

        // Remember that control events will still be processed here
        if (dirtyContainerItems != null) {
            for (ContainerItem containerItem : dirtyContainerItems) {
                containerItem.calculate();
            }
        }
    }
}
