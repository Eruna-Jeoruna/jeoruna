package com.prography1.eruna.util.scheduler.job;

import lombok.Builder;
import lombok.Getter;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.Trigger;

@Getter
public class JobConfig {
    private final Class<? extends  Job> jobClass;
    private final Trigger trigger;



    @Builder
    public JobConfig(Class<? extends  Job> jobClass, Trigger trigger) {
        this.jobClass = jobClass;
        this.trigger = trigger;
    }
}
