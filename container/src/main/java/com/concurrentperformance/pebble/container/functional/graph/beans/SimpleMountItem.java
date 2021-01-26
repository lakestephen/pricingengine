package com.concurrentperformance.pebble.container.functional.graph.beans;

public class SimpleMountItem {

    private long id;
    private long hostContainerId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHostContainerId() {
        return hostContainerId;
    }

    public void setHostContainerId(long hostContainerId) {
        this.hostContainerId = hostContainerId;
    }
}
