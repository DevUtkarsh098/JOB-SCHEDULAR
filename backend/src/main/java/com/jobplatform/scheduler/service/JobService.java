package com.jobplatform.scheduler.service;

import com.jobplatform.scheduler.entity.Job;
import com.jobplatform.scheduler.repository.JobRepository;
import com.jobplatform.scheduler.event.JobCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final ApplicationEventPublisher eventPublisher;

    public JobService(JobRepository jobRepository, ApplicationEventPublisher eventPublisher) {
        this.jobRepository = jobRepository;
        this.eventPublisher = eventPublisher;
    }

    public Job createJob(Job job) { 
        Job savedJob = jobRepository.save(job);
        eventPublisher.publishEvent(new JobCreatedEvent(this, savedJob));
        return savedJob;
    }
    public List<Job> getAllJobs() { return jobRepository.findAll(); }
}
