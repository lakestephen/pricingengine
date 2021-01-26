package com.concurrentperformance.pebble.controllerclient.api.graph;



public interface ClientGraphServiceS2CApi {

	public void clientGraphService_mountCreated(long mountId, String path);
	public void clientGraphService_mountHosted(long mountId, long containerId);
	public void clientGraphService_mountDehosted(long id);

	public void clientGraphService_graphItemCreated(long graphItemId, String path, String calculationType, String outputEventId);

	//TODO should be in different service? or should each section of the UI have a dedicated service that provides all its agregated needs?
	public void clientGraphService_eventValueUpdated(String eventId, int value);
	
}
