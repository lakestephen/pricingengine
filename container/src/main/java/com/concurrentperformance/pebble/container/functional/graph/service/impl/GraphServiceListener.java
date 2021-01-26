package com.concurrentperformance.pebble.container.functional.graph.service.impl;

import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItem;

public interface GraphServiceListener {

    void graphService_graphItemCreated(GraphItem graphItem);

    void graphService_graphItemPersisted(long graphItemId, boolean persisted);
}
