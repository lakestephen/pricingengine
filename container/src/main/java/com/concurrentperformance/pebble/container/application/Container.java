package com.concurrentperformance.pebble.container.application;

import com.concurrentperformance.pebble.comms.client.ConnectService;
import com.concurrentperformance.pebble.container.functional.items.service.ContainerItemsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Container {

    private final Log log = LogFactory.getLog(this.getClass());

    private ConnectService hubConnectService;
    private ContainerItemsService containerItemsService;

    public void start() {
        log.info("Starting to connect to hub");
        boolean successfullConnectToHub = hubConnectService.start();

        if (!successfullConnectToHub) {
            log.fatal("Cant connect to hub. Exiting");
            System.exit(0);
        }
        containerItemsService.start();
    }

    public void setHubConnectService(ConnectService hubConnectService) {
        this.hubConnectService = hubConnectService;
    }

    public void setContainerItemsService(ContainerItemsService containerItemsService) {
        this.containerItemsService = containerItemsService;
    }
}