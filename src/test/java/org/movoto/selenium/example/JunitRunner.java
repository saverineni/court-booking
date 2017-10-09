package org.movoto.selenium.example;

import org.junit.runner.JUnitCore;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JunitRunner {
    static Logger logger = LoggerFactory.getLogger(JunitRunner.class);

    public JunitRunner() {
    }

    public static void main(String args[]) throws JobExecutionException {
        logger.info("Running cronjob .....");
        JUnitCore junit = new JUnitCore();
        JUnitCore.main("org.movoto.selenium.example.CourtBookingHeadlessTest");
    }
}