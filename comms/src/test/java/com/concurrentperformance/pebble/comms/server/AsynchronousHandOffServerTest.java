package com.concurrentperformance.pebble.comms.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.concurrentperformance.pebble.comms.server.exception.AsynchHandOffException;


public class AsynchronousHandOffServerTest {

	private final Log log = LogFactory.getLog(this.getClass());
	
	private Mockery context = new Mockery();
	
	private AsynchHandOffSocketHandler nullHandler;
	
	@Before
	public void setup() {
		nullHandler = context.mock(AsynchHandOffSocketHandler.class);
		context.checking(new Expectations() {{
		    oneOf (nullHandler).handOffNewSocketConnection(with(aNonNull(Socket.class)));
		    allowing (nullHandler).getServerThreadName(); will(returnValue("TEST-ThreadName"));
		}});
	}
	
	@Test
	public void startsAndStops() throws InterruptedException {

		AsynchHandOffSocketServer objectServer = new AsynchHandOffSocketServer(5001);
		objectServer.setHandler(nullHandler);
		assertTrue(objectServer.isClosed());
		Thread.sleep(100);

		objectServer.startListening();
		assertFalse(objectServer.isClosed());
		Thread.sleep(100);
		
		objectServer.stopListening();
		assertTrue(objectServer.isClosed());

	}	
	
	@Test
	public void startsAndStopsNoDelays() {

		AsynchHandOffSocketServer objectServer = new AsynchHandOffSocketServer(5002);
		objectServer.setHandler(nullHandler);

		assertTrue(objectServer.isClosed());

		objectServer.startListening();
		assertFalse(objectServer.isClosed());
		
		objectServer.stopListening();
		assertTrue(objectServer.isClosed());
	}	
	
	@Test
	public void acceptsIncommingConnection() throws IOException {
		final int port = 5003;
		AsynchHandOffSocketServer objectServer = new AsynchHandOffSocketServer(port);
		objectServer.setHandler(nullHandler);
		objectServer.startListening();
		
		//make connection with listening server
		Socket socket = new Socket("127.0.0.1", port);
		assertNotNull(socket);
		assertTrue(socket.isConnected());
		assertTrue(socket.isBound());
		assertFalse(socket.isClosed());
	}

	@Test(expected=AsynchHandOffException.class)
	public void throwsExceptionPortUnavailable() {
		final int port = 5004;
		AsynchHandOffSocketServer objectServer = new AsynchHandOffSocketServer(port);
		objectServer.setHandler(nullHandler);
		objectServer.startListening();
		
		AsynchHandOffSocketServer objectServerOnSamePort = new AsynchHandOffSocketServer(port);
		objectServerOnSamePort.setHandler(nullHandler);
		objectServerOnSamePort.startListening();
	
	}
	
	@Test
	public void throwsExceptionWhenStartingTwice()  {

		AsynchHandOffSocketServer objectServer = new AsynchHandOffSocketServer(5005);
		objectServer.setHandler(nullHandler);

		objectServer.startListening();
		try {
			objectServer.startListening();
			fail("Should have thrown exception");
		} catch (AsynchHandOffException e) {
			log.info("Expected exception : " + e);
		}
	}
	
	@Test
	public void throwsExceptionWhenStoppingTwice() throws IOException  {

		AsynchHandOffSocketServer objectServer = new AsynchHandOffSocketServer(5006);
		objectServer.setHandler(nullHandler);

		objectServer.startListening();
		objectServer.stopListening();
		try {
			objectServer.stopListening();
			fail("Should have thrown exception");
		} catch (AsynchHandOffException e) {
			log.info("Expected exception : " + e);
		}
	}


	@Test
	public void throwsExceptionWhenStoppingBeforeStarting() {
	
		AsynchHandOffSocketServer objectServer = new AsynchHandOffSocketServer(5007);
		try {
			objectServer.stopListening();
			fail("Should have thrown exception");
		} catch (AsynchHandOffException e) {
			log.info("Expected exception : " + e);
		}
	}
	
	@Test(expected=AsynchHandOffException.class)
	public void throwsExceptionWhenNoHandlerSet() throws Exception {
	
		AsynchHandOffSocketServer objectServer = new AsynchHandOffSocketServer(5008);
		objectServer.startListening();
	}


}
