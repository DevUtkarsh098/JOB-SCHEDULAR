package com.jobplatform.scheduler.event;

import com.jobplatform.scheduler.entity.Job;
import org.springframework.context.ApplicationEvent;

public class JobCreatedEvent extends ApplicationEvent {
    private final Job job;

    public JobCreatedEvent(Object source, Job job) {
        super(source);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
