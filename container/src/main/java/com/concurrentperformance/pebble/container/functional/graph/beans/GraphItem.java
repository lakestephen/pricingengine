package com.concurrentperformance.pebble.container.functional.graph.beans;

import java.util.List;


public class GraphItem {

    private long id;
    private String path;
    private String calculation;
    private List<String> inputEventIds;
    private String outputEventId;
    private boolean persisted = false;
    private GraphItemValueInt graphItemValueInt;
    private long mountId; //TODO This might be better as a proper relationship once the grap items are being written by the container.

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public long getMountId() {
        return mountId;
    }

    public void setMountId(long mountId) {
        this.mountId = mountId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public List<String> getInputEventIds() {
        return inputEventIds;
    }

    public void setInputEventIds(List<String> inputEventIds) {
        this.inputEventIds = inputEventIds;
    }

    public String getOutputEventId() {
        return outputEventId;
    }

    public void setOutputEventId(String outputEventId) {
        this.outputEventId = outputEventId;
    }

    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    public GraphItemValueInt getGraphItemValueInt() {
        return graphItemValueInt;
    }

    public void setGraphItemValueInt(GraphItemValueInt graphItemValueInt) {
        this.graphItemValueInt = graphItemValueInt;
    }


    @Override
    public String toString() {
        return "GraphItem [id=" + id + ", path=" + path
                + ", calculation=" + calculation + ", inputEventIds="
                + inputEventIds + ", outputEventId=" + outputEventId + "]";
    }

}
