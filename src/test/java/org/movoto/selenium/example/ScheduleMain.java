package org.movoto.selenium.example;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by suresh.averineni on 22/09/2017.
 */
public class ScheduleMain {

    static Logger logger = LoggerFactory.getLogger(ScheduleMain.class);

    public static void main(String args[]) throws SchedulerException {
        // Create a new job with the SampleJob class
        JobDetail job = JobBuilder.newJob(SchedulerJob.class).withIdentity("mySampleJob", "sampleGroup").build();

       // Trigger the job to run on the next round minute
        Trigger trigger = TriggerBuilder.newTrigger()
                // Create an identity for the trigger
                .withIdentity("mySampleTrigger", "sampleGroup")
                // Specify a schedule
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 10 15 ? * 6")) //15:10 on every Friday
                // Build the trigger
                .build();

        // Get a scheduler instance
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        // Start the scheduler
        scheduler.start();
        // Add the job and trigger to the scheduler
        scheduler.scheduleJob(job, trigger);

    }
}
