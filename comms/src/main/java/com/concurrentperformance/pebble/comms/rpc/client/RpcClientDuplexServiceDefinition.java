package com.concurrentperformance.pebble.comms.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.comms.rpc.connection.IncommingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;

public class RpcClientDuplexServiceDefinition<S2CAPI, S2C extends RpcIncommingServiceSupport<C2S>, 
												C2SAPI, C2S extends RpcClientC2SSupport> {

	protected Class<S2CAPI> serviceOutgoingC2SAPI;
	protected Class<S2C> serviceOutgoingC2S;
	protected Class<C2SAPI> serviceIncommingS2CAPI;
	protected Class<C2S> serviceIncommingS2C;

	public Object buildProxy(RpcConnection connection) {
		if(serviceOutgoingC2S == null) {
            throw new IllegalArgumentException("Property 'serviceC2S' is required");
        } 

        if (!RpcIncommingServiceSupport.class.isAssignableFrom(serviceOutgoingC2S)) {
            throw new IllegalArgumentException("Property 'serviceC2S' must implement RpcClientServiceListenerSupport. Passed [" + serviceOutgoingC2S + "]");
        }

        if (!serviceOutgoingC2SAPI.isAssignableFrom(serviceOutgoingC2S)) {
            throw new IllegalArgumentException("Property 'serviceC2S' must implement 'serviceC2SAPI'. Passed serviceC2SAPI [" + serviceOutgoingC2SAPI + "], serviceC2S [" + serviceOutgoingC2S + "]");
        }

        if (!RpcClientC2SSupport.class.isAssignableFrom(serviceIncommingS2C)) {
            throw new IllegalArgumentException("Property 'serviceS2C' must implement 'RpcClientServiceListenerConnectionSupport'. Passed [" + serviceIncommingS2C + "]");
        }

        if (!serviceIncommingS2CAPI.isAssignableFrom(serviceIncommingS2C)) {
            throw new IllegalArgumentException("Property 'serviceS2C' must implement 'serviceS2CAPI'. Passed serviceS2CAPI [" + serviceIncommingS2CAPI + "], serviceS2C [" + serviceIncommingS2C + "]");
        }

        // Build the invocation handler, and the proxy. 
        /*
         * NOTE: The proxy implements the S2C service (for use in the app) 
         * and the C2S service(for use when callbacks come back from the remote client)
         */
        InvocationHandler handler = new ListeningBlockingMethodInvocationHandler<C2SAPI>(connection, serviceOutgoingC2SAPI.getSimpleName(), serviceIncommingS2CAPI);
        
        Object proxy = Proxy.newProxyInstance(serviceOutgoingC2S.getClassLoader(),
								new Class[] { serviceOutgoingC2S, serviceIncommingS2C }, handler);
		return proxy;
	}
	

	protected IncommingServiceDefinition<C2SAPI, C2S> buildIncommingServiceDefinition(Object proxy) {
		IncommingServiceDefinition<C2SAPI, C2S> incommingServiceDefinition = new IncommingServiceDefinition<C2SAPI, C2S>();
		incommingServiceDefinition.setIncommingService((C2S)proxy);
		incommingServiceDefinition.setServiceAPIInterface(serviceIncommingS2CAPI);
		incommingServiceDefinition.setIncommingServiceInterface(serviceIncommingS2C);
		return incommingServiceDefinition;
	}
	
    public void setServiceOutgoingC2SAPI(Class<S2CAPI> serviceOutgoingC2SAPI){
        this.serviceOutgoingC2SAPI = serviceOutgoingC2SAPI;
    }
    
    public void setServiceOutgoingC2S(Class<S2C> serviceOutgoingC2S){
        this.serviceOutgoingC2S = serviceOutgoingC2S;
    }
    
    public Class<S2C> getServiceOutgoingC2S() {
        return serviceOutgoingC2S;
    }

    public void setServiceIncommingS2CAPI(Class<C2SAPI> serviceIncommingS2CAPI){
        this.serviceIncommingS2CAPI = serviceIncommingS2CAPI;
    }

    public void setServiceIncommingS2C(Class<C2S> serviceIncommingS2C){
        this.serviceIncommingS2C = serviceIncommingS2C;
    }

}
