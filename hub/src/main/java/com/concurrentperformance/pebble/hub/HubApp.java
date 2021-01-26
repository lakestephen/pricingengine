package com.concurrentperformance.pebble.hub;

import java.lang.management.ManagementFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.concurrentperformance.pebble.hub.application.Hub;
import com.concurrentperformance.pebble.util.thread.ThreadUncaughtExceptionHelper;

public class HubApp {

    private static final Log log = LogFactory.getLog(HubApp.class);

    public static void main(String[] args) {
        try {
            ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();

            renameLogFileAppender();
            disableLogConsoleAppender();

            String hubName = System.getProperty("pebble.hub.name");

            log.info("Starting hub app [" + hubName + "]");
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/applicationContext-hub.xml");

            // Now all the wiring has been done, then start the container.
            Hub hub = applicationContext.getBean(Hub.class);
            hub.start();

            log.info("Hub app started [" + hubName + "]");
        } catch (BeansException e) {
            log.error("Could not initialise Spring",  e);
            // Just in case logging fails.
            System.out.println("Could not initialise Spring " + e.getMessage());
        }
    }


    private static void renameLogFileAppender() {
        String hubName = System.getProperty("pebble.hub.name");
        String hubId = System.getProperty("pebble.hub.id");
        String hubInstance = System.getProperty("pebble.hub.instance");
        String process = ManagementFactory.getRuntimeMXBean().getName();
        Logger logger = Logger.getRootLogger();
        RollingFileAppender appndr = (RollingFileAppender)logger.getAppender("FILE"); //TODO pass in
        String fileName = appndr.getFile();
        String replacedFileName = fileName.replace(".log", "_" + hubName + "-" + hubId + "_I" + hubInstance + "_" + process + ".log");
        // TODO should we also get the PID in the file name?
        appndr.setFile(replacedFileName);
        appndr.activateOptions();
    }

    private static void disableLogConsoleAppender() {
        String consoleOutput = System.getProperty("console.output.on");
        if (consoleOutput == null) {
            Logger logger = Logger.getRootLogger();
            ConsoleAppender appndr = (ConsoleAppender)logger.getAppender("CONSOLE"); //TODO pass in
            appndr.setThreshold(Level.ERROR);
            appndr.activateOptions();
        }
    }

}
