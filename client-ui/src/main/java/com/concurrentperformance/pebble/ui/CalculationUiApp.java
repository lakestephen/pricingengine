package com.concurrentperformance.pebble.ui;
 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.concurrentperformance.pebble.util.thread.ThreadUncaughtExceptionHelper;

/**
 * SJL comment
 *
 * @author Stephen Lake
 *
 */
public class CalculationUiApp {
	
	private final static Log log = LogFactory.getLog(CalculationUiApp.class);

	

    public static void main(String[] args) {
    	try {
    		ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();

    		log.info("Starting UI app");
    		
    		new ClassPathXmlApplicationContext("spring/applicationContext-client-ui.xml");
    		log.info("UI app started");
    	} catch (BeansException e) {
    		log.error("Could not initialise Spring",  e);
    		// Just in case logging fails. 
    		System.out.println("Could not initialise Spring " + e.getMessage());
    	}
    }
}
