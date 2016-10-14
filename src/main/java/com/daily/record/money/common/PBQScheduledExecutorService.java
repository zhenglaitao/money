package com.daily.record.money.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public enum PBQScheduledExecutorService {

    INSTANCE;
    
    private static final Logger LOGGER = Logger.getLogger(PBQScheduledExecutorService.class);
    
    ScheduledExecutorService schedulePool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()*2+1);
    
    public void schedule(Runnable task, long delay, TimeUnit unit){
        try {
            ThreadContext.setRequestStartTime(null);
            schedulePool.schedule(task, delay, unit);
        } catch (Exception e) {
            LOGGER.error("Failed to schedule task", e);
        }
    }
}
