package com.concurrentperformance.pebble.controllerclient.api.exception;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class ClientException extends Exception { //TODO the internal controller classes should not depend on an exception here
	
	private static final long serialVersionUID = -8786534742055211616L;

	public enum ReportingLevel { //TODO should this be a global construct?
		ERROR(0), 
		WARN(2);
		
		private final int jOptionPaneValue;
	
		ReportingLevel(int jOptionPaneValue) {
			this.jOptionPaneValue = jOptionPaneValue;
		}

		public int getjOptionPaneValue() {
			return jOptionPaneValue;
		}
	}

	private final ReportingLevel reportingLevel;

	public ClientException(String message) {
    	this(message,ReportingLevel.ERROR);
    }
	
    public ClientException(String message, ReportingLevel reportingLevel) {
    	super(message);
    	this.reportingLevel = reportingLevel;
    }

	public ReportingLevel getReportingLevel() {
		return reportingLevel;
	}
}
