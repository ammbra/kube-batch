package com.example.jbatch.simplebatch;

import java.util.logging.Logger;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.BatchProperty;
import javax.inject.Inject;

/**
 * This simple batchlet waits in 1 second increments up to "wait.time.seconds".
 * wait.time.seconds can be configured as a batch property.
 *
 */
public class SimpleBatchlet extends AbstractBatchlet {

    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private volatile boolean stopRequested = false;


    @Inject
    @BatchProperty(name = "wait.time.seconds")
    String waitTimeSecondsProperty;
    private int waitTime = 20;

    @Override
    public String process() throws Exception {
        logger.info("SimpleBatchlet: process : entry");

        if (waitTimeSecondsProperty != null) {
            waitTime = Integer.parseInt(waitTimeSecondsProperty);
        }
        
        logger.info("SimpleBatchlet: process : " + String.valueOf(waitTime));

        int i;
        for (i = 0; i < waitTime && !stopRequested; ++i) {
            logger.info("SimpleBatchlet: process : " + "[" + i + "] waiting for a second...");
            Thread.sleep(1 * 1000);
        }

        String exitStatus = "SimpleBatchlet: i=" + i + ";stopRequested=" + stopRequested;
        logger.info("SimpleBatchlet: process : " + exitStatus);

        return exitStatus;
    }

    @Override
    public void stop() throws Exception {
        logger.info("SimpleBatchlet: stop ");
        stopRequested = true;
    }

}

