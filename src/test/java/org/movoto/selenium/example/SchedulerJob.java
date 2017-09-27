package org.movoto.selenium.example;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerJob implements org.quartz.Job {
    static Logger logger = LoggerFactory.getLogger(SchedulerJob.class);

    public SchedulerJob() {
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Running cronjob .....");
        JUnitCore junit = new JUnitCore();
        JUnitCore.main("org.movoto.selenium.example.CourtBookingTest");
    }
}