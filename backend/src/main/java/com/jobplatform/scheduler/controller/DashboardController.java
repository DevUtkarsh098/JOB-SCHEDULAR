package com.jobplatform.scheduler.controller;

import com.jobplatform.scheduler.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final WorkerRepository workerRepository;
    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final DeadLetterQueueRepository dlqRepository;
    private final QueueRepository queueRepository;

    public DashboardController(WorkerRepository workerRepository, 
                               JobRepository jobRepository, 
                               JobExecutionRepository jobExecutionRepository, 
                               DeadLetterQueueRepository dlqRepository, 
                               QueueRepository queueRepository) {
        this.workerRepository = workerRepository;
        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.dlqRepository = dlqRepository;
        this.queueRepository = queueRepository;
    }

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("activeWorkers", workerRepository.count());
        metrics.put("jobsExecuting", jobExecutionRepository.count()); // simplified
        metrics.put("completedToday", jobRepository.count()); // simplified
        metrics.put("failedJobs", dlqRepository.count());
        metrics.put("totalQueues", queueRepository.count());
        return metrics;
    }
}
