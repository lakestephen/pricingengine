package com.concurrentperformance.pebble.testbed;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestServerApp {

	private static Logger log = Logger.getLogger(TestServerApp.class);

    public static void main(String[] args) {
    	try {
    		new ClassPathXmlApplicationContext(args[0]);
    	} catch (BeansException e) {
    		log.error("Could not initialise Spring",  e);
    		// Just in case logging fails. 
    		System.out.println("Could not initialise Spring " + e.getMessage());
    	}
    }
	
}
