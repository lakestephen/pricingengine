package com.concurrentperformance.pebble.container;

import com.concurrentperformance.pebble.container.application.Container;
import com.concurrentperformance.pebble.util.thread.ThreadUncaughtExceptionHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.management.ManagementFactory;

public class ContainerApp {

    private static final Log log = LogFactory.getLog(ContainerApp.class);

    public static void main(String[] args) {
        try {
            ThreadUncaughtExceptionHelper.setLoggingDefaultUncaughtException();

            renameLogFileAppender();
            disableLogConsoleAppender();

            String containerName = System.getProperty("pebble.container.name");
            log.info("Starting container app [" + containerName + "]");

            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/applicationContext-container.xml");

            // Now all the wiring has been done, then start the container.
            Container cont = applicationContext.getBean(Container.class);
            cont.start();

            log.info("Container app started [" + containerName + "]");

        } catch (BeansException e) {
            log.error("Could not initialise Spring", e);
            // Just in case logging fails.
            System.out.println("Could not initialise Spring " + e.getMessage());
        }
    }

    private static void renameLogFileAppender() {
        String containerName = System.getProperty("pebble.container.name");
        String containerId = System.getProperty("pebble.container.id");
        String containerInstance = System.getProperty("pebble.container.instance");
        String process = ManagementFactory.getRuntimeMXBean().getName();
        Logger logger = Logger.getRootLogger();
        RollingFileAppender appndr = (RollingFileAppender) logger.getAppender("FILE"); //TODO pass in
        String fileName = appndr.getFile();
        String replacedFileName = fileName.replace(".log", "_" + containerName + "-" + containerId + "_I" + containerInstance + "_" + process + ".log");
        // TODO should we also get the PID in the file name?
        appndr.setFile(replacedFileName);
        appndr.activateOptions();
    }

    private static void disableLogConsoleAppender() {
        String consoleOutput = System.getProperty("console.output.on");
        if (consoleOutput == null) {
            Logger logger = Logger.getRootLogger();
            ConsoleAppender appndr = (ConsoleAppender) logger.getAppender("CONSOLE"); //TODO pass in
            appndr.setThreshold(Level.ERROR);
            appndr.activateOptions();
        }
    }
}
