package com.jobplatform.scheduler.controller;

import com.jobplatform.scheduler.entity.Job;
import com.jobplatform.scheduler.service.JobService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public Job createJob(@RequestBody Job job) { return jobService.createJob(job); }
    
    @GetMapping
    public List<Job> getJobs() { return jobService.getAllJobs(); }
}
