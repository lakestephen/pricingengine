package com.concurrentperformance.pebble.container.functional.items.service.impl;

import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItem;
import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItemValueInt;
import com.concurrentperformance.pebble.container.functional.graph.dao.GraphDao;
import com.concurrentperformance.pebble.container.functional.graph.service.impl.GraphService;
import com.concurrentperformance.pebble.container.functional.graph.service.impl.GraphServiceListener;
import com.concurrentperformance.pebble.container.functional.items.beans.ContainerItem;
import com.concurrentperformance.pebble.container.functional.items.service.ContainerItemsService;
import com.concurrentperformance.pebble.container.functional.output.OutputEventProcessor;
import com.concurrentperformance.pebble.msgcommon.event.AdvertiseProducerAvailability;
import com.concurrentperformance.pebble.msgcommon.event.ControlEvent;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.RegisterInterestInEvent;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculation;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculationInput;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculationOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//TODO as being used by disruptor, remove synch
public class DefaultContainerItemsService implements ContainerItemsService, GraphServiceListener {

    private final Log log = LogFactory.getLog(this.getClass());

    private GraphDao graphDao; //TODO should we route calls through GraphService?


    private OutputEventProcessor outputEventProcessor;

    private long containerId;
    private Set<Long> mountIdsForThisContainer;

    /**
     * Map of eventId to interested container items
     */
    private Map<String, Set<ContainerItem>> consumersOfEventId = new ConcurrentHashMap<String, Set<ContainerItem>>();
    private Map<String, ContainerItem> producerOfEventId = new ConcurrentHashMap<String, ContainerItem>();


    @Override
    public void start() {
        log.info("Starting container load");
        loadMounts();
        loadGraphItems();
        log.info("Finished container load");
    }


    private void loadMounts() {
        log.info("Loading mounts for container [" + containerId + "]");
        // get the mounts we are interested in
        Set<Long> mountIds = graphDao.getMountIdsForContainerId(containerId);
        mountIdsForThisContainer = new HashSet<Long>(mountIds);
        if (mountIdsForThisContainer.size() == 0) {
            log.info("No mounts for container [" + containerId + "]");
        } else {
            log.info("Loaded mountIds [" + mountIdsForThisContainer + "] for container [" + containerId + "]");
        }
    }

    private void loadGraphItems() {
        for (Long mountId : mountIdsForThisContainer) {
            loadGraphItemsForMountId(mountId);
        }
    }

    void loadGraphItemsForMountId(long mountId) {
        log.info("Loading graph items for mount [" + mountId + "]");

        Set<GraphItem> graphItems = graphDao.getAllGraphItemsForMount(mountId);
        log.info("Loaded graph items [" + containerId + "->" + mountId + "], [" + graphItems + "]");

        for (GraphItem graphItem : graphItems) {
            loadGraphItem(graphItem);
        }
    }

    private void loadGraphItem(GraphItem graphItem) {
        try {
            GraphCalculation calculation = buildCalculation(graphItem);
            ContainerItem containerItem = buildContainerItem(graphItem, calculation);

            if (calculation instanceof GraphCalculationInput) {
                configureInputs(graphItem, containerItem);
            }

            if (calculation instanceof GraphCalculationOutput) {
                configureOutputs(graphItem, containerItem);
            }

            containerItem.start();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            log.error("Cant create calculation item for [" + graphItem + "]", e);
        }
    }

    private GraphCalculation buildCalculation(GraphItem graphItem)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        String calculationClassName = graphItem.getCalculation();
        Class<?> calculationClass = Class.forName(calculationClassName);
        GraphCalculation calculation = (GraphCalculation) calculationClass.newInstance();
        return calculation;
    }

    private ContainerItem buildContainerItem(GraphItem graphItem,
                                             GraphCalculation calculation) {
        String path = graphItem.getPath();
        ContainerItem containerItem = new ContainerItem(path, calculation);
        return containerItem;
    }

    private void configureInputs(GraphItem graphItem, ContainerItem containerItem) {
        // Build the container item
        List<String> inputEventIds = graphItem.getInputEventIds();
        containerItem.setInputDetails(inputEventIds);

        // add inputs to the map of interested items
        //TODO need to do checking of input and output types. - can we do this at this stage?
        for (String inputEventId : inputEventIds) {
            // add to consumer map
            Set<ContainerItem> containerItems = consumersOfEventId.get(inputEventId);
            if (containerItems == null) {
                containerItems = new HashSet<ContainerItem>();
                consumersOfEventId.put(inputEventId, containerItems); //TODO if not in disruptor, this should use CAS operations
            }

            // add to producer map
            containerItems.add(containerItem);
            informContainerOfConsumerInterest(inputEventId);

        }
    }

    private void informContainerOfConsumerInterest(String inputEventId) {
        log.info("Tell hub we need to consume event [" + inputEventId + "]");
        ControlEvent event = new RegisterInterestInEvent(inputEventId, containerId);
        outputEventProcessor.processControlEvent(event);
    }

    private void configureOutputs(GraphItem graphItem, ContainerItem containerItem) {
        String outputEventId = graphItem.getOutputEventId();
        containerItem.setOutputDetails(outputEventProcessor, outputEventId);

        // Add to producer map.
        producerOfEventId.put(outputEventId, containerItem);

        // Tell container
        informContainerOfOutputAvailability(outputEventId);

        if (graphItem.isPersisted()) {
            GraphItemValueInt value = graphDao.getGraphValueInt(graphItem.getId());
            if (value != null) {
                containerItem.setValueArtificially(value.getValue());
            }
        }
    }

    private void informContainerOfOutputAvailability(String outputEventId) {
        log.info("Tell hub we are producing [" + outputEventId + "]");
        ControlEvent event = new AdvertiseProducerAvailability(outputEventId, containerId);
        outputEventProcessor.processControlEvent(event);
    }

    @Override
    public Set<ContainerItem> findConsumerItems(Event event) {
        String id = event.getId();
        Set<ContainerItem> consumerItems = consumersOfEventId.get(id);
        if (consumerItems == null) {
            consumerItems = Collections.emptySet();
        }
        return consumerItems;
    }

    @Override
    public ContainerItem findProducerItem(Event event) {
        String id = event.getId();
        ContainerItem producerItems = producerOfEventId.get(id);
        return producerItems;
    }

    @Override
    public void setValueArtificially(String eventId, int value) {
        ContainerItem containerItem = producerOfEventId.get(eventId);
        if (containerItem != null) {
            containerItem.setValueArtificially(value);
        }
    }

    @Override
    public void graphService_graphItemCreated(GraphItem graphItem) {
        log.info("Adding new " + graphItem);
        loadGraphItem(graphItem);
    }


    @Override
    public void graphService_graphItemPersisted(long graphItemId,
                                                boolean persisted) {
        // TODO Auto-generated method stub

    }

    public void setContainerId(long containerId) {
        this.containerId = containerId;
    }

    public void setGraphDao(GraphDao graphDao) {
        this.graphDao = graphDao;
    }

    public void setGraphService(GraphService graphService) {
        graphService.register(this);
    }

    public void setOutputEventProcessor(OutputEventProcessor outputEventProcessor) {
        this.outputEventProcessor = outputEventProcessor;
    }


}
