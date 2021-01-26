package com.concurrentperformance.pebble.comms.rpc.connection;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class OutgoingServiceDefinition<OUTAPI, OUT> { 
	
	private OUT outgoingService;
	private Class<OUTAPI> outgoingServiceInterfaceAPI;	


    public void setOutgoingService(OUT outgoingService) {
        this.outgoingService = outgoingService;
    }
	
	public final OUT getOutgoingService() {
		return outgoingService;
	}
	
    /**
     * Set the interface that the service will be retrieved and assessed by.
     */
	public void setOutgoingServiceAPIInterface(Class<OUTAPI> outgoingServiceInterfaceAPI)
    {
        if(outgoingServiceInterfaceAPI == null || !outgoingServiceInterfaceAPI.isInterface()) {
            throw new IllegalArgumentException("'outgoingServiceInterfaceAPI' must be an interface");
        }
        
        this.outgoingServiceInterfaceAPI = outgoingServiceInterfaceAPI;
    }

    public Class<OUTAPI> getOutgoingServiceAPIInterface() {
    	return outgoingServiceInterfaceAPI;
    }
    

	@Override
	public String toString() {
		return outgoingServiceInterfaceAPI.getSimpleName();
	}	
}