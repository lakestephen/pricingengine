package com.concurrentperformance.pebble.comms.rpc.server.service;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;


public abstract class RpcServerDuplexServiceDefinition
						<INAPI extends RpcServerS2CSupport & RpcIncommingServiceSupport<OUTAPI>,OUTAPI> {

	private Class<INAPI> serviceIncommingC2SAPI;
	private Class<OUTAPI> serviceOutgoingS2CAPI;
	
	public abstract INAPI createC2SService();

	public Class<INAPI> getServiceIncommingC2SAPI() {
		return serviceIncommingC2SAPI;
	}
	
	public Class<OUTAPI> getServiceOutgoingS2CAPI() {
		return serviceOutgoingS2CAPI;
	}

	public String getServiceOutgoingS2CAPIIdentifier() {
		return serviceOutgoingS2CAPI.getSimpleName();
	}
	
	public void setServiceIncommingC2SAPI(Class<INAPI> serviceIncommingC2SAPI){
        this.serviceIncommingC2SAPI = serviceIncommingC2SAPI;
    }
    
    public void setServiceOutgoingS2CAPI(Class<OUTAPI> serviceOutgoingS2CAPI){
        this.serviceOutgoingS2CAPI = serviceOutgoingS2CAPI;
    }
}
