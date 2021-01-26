package com.concurrentperformance.pebble.container.functional.graph.service.impl;

import com.concurrentperformance.pebble.container.controller.ContainerGraphServiceC2S;
import com.concurrentperformance.pebble.container.controller.ContainerGraphServiceS2C;
import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItem;
import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItemValueInt;
import com.concurrentperformance.pebble.container.functional.graph.dao.GraphDao;
import com.concurrentperformance.pebble.controllercontainer.api.graph.exception.ContainerGraphServiceException;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculation;
import com.concurrentperformance.pebble.msgcommon.graph.GraphCalculationInput;
import com.concurrentperformance.pebble.util.service.AsynchServiceListenerSupport;
import com.concurrentperformance.pebble.util.service.ServiceListenerSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Set;

public class GraphService extends AsynchServiceListenerSupport<GraphServiceListener>
        implements ContainerGraphServiceS2C,
        ServiceListenerSupport<GraphServiceListener> {

    private final Log log = LogFactory.getLog(this.getClass());

    private ContainerGraphServiceC2S containerGraphService;
    private GraphDao graphDao;

    @Override
    public long addGraphItem(long mountId, String graphPath,
                             Class<? extends GraphCalculation> graphCalculation,
                             List<String> inputEventIds, String outputEventId)
            throws ContainerGraphServiceException {
        log.info("Add [" + graphCalculation.getSimpleName() + " ] to [" + graphPath + "]");

        validate(graphPath, graphCalculation, inputEventIds);

        GraphItem graphItem = new GraphItem();
        graphItem.setMountId(mountId);
        graphItem.setPath(graphPath);
        graphItem.setCalculation(graphCalculation.getCanonicalName());
        graphItem.setInputEventIds(inputEventIds);
        graphItem.setOutputEventId(outputEventId);
        try {
            graphDao.persistGraphItem(graphItem);
        } catch (DataIntegrityViolationException e) {
            throw new ContainerGraphServiceException("Cant persist [" + graphPath + "] as it already exists.");
        }

        containerGraphService.graphService_graphItemCreated(graphItem.getId(), graphPath, graphCalculation.getCanonicalName(), outputEventId);
        fireGraphItemCreated(graphItem);

        return graphItem.getId();
    }

    private void fireGraphItemCreated(final GraphItem graphItem) {
        submitTask(new Runnable() {
            @Override
            public void run() {
                for (GraphServiceListener listener : getListeners()) {
                    listener.graphService_graphItemCreated(graphItem);
                }
            }
        });
    }

    @Override
    public void populateAllGraphItems(long mountId) {
        //TODO this needs actually to register specific paths for updates, and keep in client.
        // TODO as a temporary bodge we dont actually register, but just return everything.

        Set<GraphItem> allGraphItemsForMount = graphDao.getAllGraphItemsForMount(mountId);
        log.info("Got the following items [" + allGraphItemsForMount + "] for mount [" + mountId + "]");
        for (GraphItem graphItem : allGraphItemsForMount) {
            containerGraphService.graphService_graphItemCreated(graphItem.getId(), graphItem.getPath(), graphItem.getCalculation(), graphItem.getOutputEventId());
        }
    }

    @Override
    public void setGraphValue(long id, int value) throws ContainerGraphServiceException {
        GraphItem graphItem = graphDao.findGraphItem(id);
        if (graphItem == null) {
            throw new ContainerGraphServiceException("Cant find Graph item id [" + id + "]");
        }

        if (!graphItem.isPersisted()) {
            throw new ContainerGraphServiceException("Graph item is not persisted id [" + id + "]. Call setPersistable first.");
        }

        GraphItemValueInt intValue = new GraphItemValueInt();
        intValue.setGraphItemId(id);
        intValue.setValue(value);
        graphDao.persistGraphValue(intValue);

        // TODO notify
    }

    @Override
    public void setPersisted(long id, boolean persisted) throws ContainerGraphServiceException {
        GraphItem graphItem = graphDao.findGraphItem(id);

        if (graphItem == null) {
            throw new ContainerGraphServiceException("Cant find Graph item id [" + id + "]");
        }

        graphItem.setPersisted(persisted);
        graphDao.persistGraphItem(graphItem);

        //TODO notify
    }

    private void validate(String graphPath, Class<? extends GraphCalculation> graphCalculation, List<String> inputEventIds) throws ContainerGraphServiceException {
        //TODO we could cache these to save the object create, OR if this is happening in the container, then instantiate on teh graph, and check, then persist.
        //TODO validate input type.
        try {
            GraphCalculation calculation = graphCalculation.newInstance();
            if (calculation instanceof GraphCalculationInput) {
                GraphCalculationInput calculationInput = (GraphCalculationInput) calculation;
                if (calculationInput.getInputEventDefinition().length != inputEventIds.size()) {
                    throw new ContainerGraphServiceException("Failed to create [" + graphPath + "] as passed event ids [" + inputEventIds + "] does not match required inputs [" + calculationInput + "]");
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ContainerGraphServiceException("Can't create instance of [" + graphCalculation + "] for validation" + e.getMessage());
        }
    }


    @Override
    public void connectionSupport_connectionStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    public void connectionSupport_connectionStopped() {
        // TODO Auto-generated method stub

    }

    public void setContainerGraphService(ContainerGraphServiceC2S containerGraphService) {
        this.containerGraphService = containerGraphService;
        // we are listening to events over the wire.
        containerGraphService.register(this);
    }

    public void setGraphDao(GraphDao graphDao) {
        this.graphDao = graphDao;
    }

}
