package com.concurrentperformance.pebble.controller;

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
public final class ControllerApp { // TODO think about where this should be in the package structure
	
	private static final Log log = LogFactory.getLog(ControllerApp.class);

    public static void main(String[] args) {
	
    	try {
    		ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();

    		log.info("Starting Controller app ");

    		new ClassPathXmlApplicationContext("spring/applicationContext-controller.xml");

    		log.info("Controller app started");
    	} catch (BeansException e) {
    		log.error("Could not initialise Spring",  e);
    		// Just in case logging fails. 
    		System.out.println("Could not initialise Spring " + e.getMessage());
    	}
    }
}
