package com.jobplatform.scheduler.scheduler;

import com.jobplatform.scheduler.entity.Job;
import com.jobplatform.scheduler.repository.JobRepository;
import com.jobplatform.scheduler.worker.WorkerManager;
import com.jobplatform.scheduler.event.JobCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JobSchedulerEngine {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JobSchedulerEngine.class);
    
    private final JobRepository jobRepository;
    private final WorkerManager workerManager;

    public JobSchedulerEngine(JobRepository jobRepository, WorkerManager workerManager) {
        this.jobRepository = jobRepository;
        this.workerManager = workerManager;
    }

    @EventListener
    public void handleJobCreatedEvent(JobCreatedEvent event) {
        Job job = event.getJob();
        if ("PENDING".equals(job.getStatus())) {
            log.info("Event received, claiming job: " + job.getId());
            job.setStatus("IN_PROGRESS");
            jobRepository.save(job);
            
            workerManager.executeJob(job);
        }
    }
}
