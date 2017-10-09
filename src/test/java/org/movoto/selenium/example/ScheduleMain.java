package org.movoto.selenium.example;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleMain {

    static Logger logger = LoggerFactory.getLogger(ScheduleMain.class);

    public static void main(String args[]) throws SchedulerException {

        logger.info("Running the Quartz scheduler .....");
        JobDetail job = JobBuilder.newJob(SchedulerJob.class).withIdentity("mySampleJob", "sampleGroup").build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("mySampleTrigger", "sampleGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule("00 00 06 ? * 5,6,1")) // Sun - Sat(1 - 7)
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }
}
