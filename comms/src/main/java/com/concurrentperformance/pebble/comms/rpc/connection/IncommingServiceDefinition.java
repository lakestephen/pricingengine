package com.concurrentperformance.pebble.comms.rpc.connection;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class IncommingServiceDefinition<INAPI, IN> { 
	
	private IN incommingService;
	private Class<INAPI> incommingServiceInterfaceAPI;	
	private Class<IN> incommingServiceInterface;	

    public void setIncommingService(IN incommingService) {
        this.incommingService = incommingService;
    }
	
    /**
	 * @return the service
	 */
	public final IN getIncommingService() {
		return incommingService;
	}
    
    public void setIncommingServiceInterface(Class<IN> incommingServiceInterface)
    {
        if(incommingServiceInterface == null || !incommingServiceInterface.isInterface()) {
            throw new IllegalArgumentException("'incommingServiceInterface' must be an interface");
        }
        
        this.incommingServiceInterface = incommingServiceInterface;
    }

    
    public void setServiceAPIInterface(Class<INAPI> incommingServiceInterfaceAPI)
    {
        if(incommingServiceInterfaceAPI == null || !incommingServiceInterfaceAPI.isInterface()) {
            throw new IllegalArgumentException("'incommingServiceInterfaceAPI' must be an interface");
        }
        
        this.incommingServiceInterfaceAPI = incommingServiceInterfaceAPI;
    }
    
	/**
	 * @return the serviceInterface
	 */
	public final Class<IN> getIncommingServiceInterface() {
		return incommingServiceInterface;
	}
    
	/**
	 * @return the serviceIdentifier
	 */
	public final String getIncommingServiceIdentifier() {
		if (incommingServiceInterfaceAPI != null) {
			return incommingServiceInterfaceAPI.getSimpleName();
		}
		else {
			return incommingServiceInterface.getSimpleName();
		}
			
	}

	@Override
	public String toString() {
		return getIncommingServiceIdentifier();
	}	
}