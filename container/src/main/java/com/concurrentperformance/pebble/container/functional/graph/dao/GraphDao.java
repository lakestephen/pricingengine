package com.concurrentperformance.pebble.container.functional.graph.dao;

import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItem;
import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItemValueInt;

import java.util.Set;


public interface GraphDao { //TODO should this be GtaphItemDao?

    void persistGraphItem(GraphItem graphItem);

    Set<Long> getMountIdsForContainerId(long containerId);

    Set<GraphItem> getAllGraphItemsForMount(long mountId);

    GraphItem findGraphItem(long id);

    // TODO this value items should be in s separate DAO and service.
    void persistGraphValue(GraphItemValueInt value);

    //TODO this is not a scale-able call.
    Set<GraphItemValueInt> getAllGraphValueInt();

    GraphItemValueInt getGraphValueInt(long graphitemId);


}
