package com.concurrentperformance.pebble.util.thread;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Last resort exception handler.
 * 
 * @author Stephen Lake
 */
public class ThreadUncaughtExceptionHelper {

	private static final Log log = LogFactory.getLog(LogginUncaughtExceptionHandler.class);
	
	public static void setLoggingDefaultUncaughtException() {
		Thread.setDefaultUncaughtExceptionHandler(new LogginUncaughtExceptionHandler());
	}
	
	private static class LogginUncaughtExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread thread, Throwable e) {
			try {
				log.error("UNCAUGHT EXCEPTION in thread [" + thread + "]", e);
			} catch (Throwable t) {
				// Swallow!! If any exception escapes here could get infinite loop. 
			}			
		}		
	}	
}
