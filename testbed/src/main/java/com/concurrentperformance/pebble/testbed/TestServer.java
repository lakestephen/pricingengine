package com.concurrentperformance.pebble.testbed;

public class TestServer {

/*
	private Logger log = Logger.getLogger(this.getClass());

	private RemoteClientManagement remoteClientManagement;

	public void start() {
		log.info("Testbed started");
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				String lastTxt = "";

				while (true) {
					String txt = remoteClientManagement.getAllRemoteClients().toString();
					if (!lastTxt.equals(txt )) {
						lastTxt = txt ;
						log.warn("Available clients: " + lastTxt);
						
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						log.error("TODO ", e);
					}
				}
				
			}
		};
		
		new Thread(r, "log available clients").start();
	}

	public void setAvailableClients(RemoteClientManagement remoteClientManagement) {
		this.remoteClientManagement = remoteClientManagement;
	}
*/
}