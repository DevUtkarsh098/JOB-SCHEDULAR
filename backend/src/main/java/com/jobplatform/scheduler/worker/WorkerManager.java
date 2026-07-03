package com.jobplatform.scheduler.worker;

import com.jobplatform.scheduler.entity.Job;
import com.jobplatform.scheduler.repository.JobRepository;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WorkerManager {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WorkerManager.class);
    
    // Virtual Threads executor for job execution
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final JobRepository jobRepository;

    public WorkerManager(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void executeJob(Job job) {
        executorService.submit(() -> {
            try {
                log.info("Worker processing job: " + job.getId());
                // Simulated work
                Thread.sleep(1500);
                
                job.setStatus("COMPLETED");
                jobRepository.save(job);
                log.info("Worker finished job: " + job.getId());
            } catch (Exception e) {
                log.error("Job failed: " + job.getId(), e);
                job.setStatus("FAILED");
                jobRepository.save(job);
            }
        });
    }
}
