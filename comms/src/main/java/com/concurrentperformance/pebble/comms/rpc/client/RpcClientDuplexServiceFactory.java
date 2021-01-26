package com.concurrentperformance.pebble.comms.rpc.client;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.concurrentperformance.pebble.comms.rpc.common.RpcIncommingServiceSupport;
import com.concurrentperformance.pebble.comms.rpc.connection.IncommingServiceDefinition;
import com.concurrentperformance.pebble.comms.rpc.connection.RpcConnection;

/**
 * A factory bean for use on the client side that defines both a 
 * service, and corresponding listener. It will create a 
 * service proxy for use in the wider application but will also 
 * allow the registration of listeners, and will forward the listener 
 * calls to all said registered listeners. 
 * 
 * @author Stephen Lake
 *
 * @param <S2CAPI> The wire API interface
 * @param <S2C> the type of service we create a proxy of - must extend RpcClientServiceListenerSupport
 * @param <C2S> The type of listener we support registration for
 */
public class RpcClientDuplexServiceFactory<S2CAPI, S2C extends RpcIncommingServiceSupport<C2S>, 
												C2SAPI, C2S extends RpcClientC2SSupport> 
			extends RpcClientDuplexServiceDefinition<S2CAPI, S2C, C2SAPI, C2S >
			implements FactoryBean<S2C>, InitializingBean { 

	private RpcConnection connection;  

	private S2C proxy;
	
	@Override
	public S2C getObject() throws Exception {
		return proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return serviceOutgoingC2S;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		proxy = (S2C)buildProxy(connection);
		C2S  C2SProxy = (C2S)proxy ;		

		IncommingServiceDefinition<C2SAPI, C2S> incommingServiceDefinition = buildIncommingServiceDefinition(C2SProxy);
		connection.addIncommingServiceDefinition(incommingServiceDefinition);
		
	}
	
	public void setConnection(RpcConnection connection) {
		this.connection = connection;
	}

	@Override
	public String toString() {
		return "RpcClientDuplexServiceFactory [connection=" + connection
				+ ", serviceC2SAPI=" + serviceOutgoingC2SAPI.getSimpleName()
				+ ", serviceC2S=" + serviceOutgoingC2S.getSimpleName()
				+ ", serviceS2CAPI=" + serviceIncommingS2CAPI.getSimpleName() + 
				  ", serviceS2C=" + serviceIncommingS2C.getSimpleName() + "]";
	}
}