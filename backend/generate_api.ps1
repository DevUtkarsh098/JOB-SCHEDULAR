$baseDir = "c:\JAVA PROJECT\Codity\backend\src\main\java\com\jobplatform\scheduler"

$files = @{
    "exception\ResourceNotFoundException.java" = @"
package com.jobplatform.scheduler.exception;
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
"@

    "exception\GlobalExceptionHandler.java" = @"
package com.jobplatform.scheduler.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error", "details", ex.getMessage()));
    }
}
"@

    "security\SecurityConfig.java" = @"
package com.jobplatform.scheduler.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().permitAll() // Allow all for quick testing
            );
        return http.build();
    }
}
"@

    "service\JobService.java" = @"
package com.jobplatform.scheduler.service;
import com.jobplatform.scheduler.entity.Job;
import com.jobplatform.scheduler.repository.JobRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    public Job createJob(Job job) { return jobRepository.save(job); }
    public List<Job> getAllJobs() { return jobRepository.findAll(); }
}
"@

    "service\QueueService.java" = @"
package com.jobplatform.scheduler.service;
import com.jobplatform.scheduler.entity.Queue;
import com.jobplatform.scheduler.repository.QueueRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final QueueRepository queueRepository;
    public Queue createQueue(Queue queue) { return queueRepository.save(queue); }
    public List<Queue> getAllQueues() { return queueRepository.findAll(); }
}
"@

    "controller\JobController.java" = @"
package com.jobplatform.scheduler.controller;
import com.jobplatform.scheduler.entity.Job;
import com.jobplatform.scheduler.service.JobService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    @PostMapping
    public Job createJob(@RequestBody Job job) { return jobService.createJob(job); }
    @GetMapping
    public List<Job> getJobs() { return jobService.getAllJobs(); }
}
"@

    "controller\QueueController.java" = @"
package com.jobplatform.scheduler.controller;
import com.jobplatform.scheduler.entity.Queue;
import com.jobplatform.scheduler.service.QueueService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;
    @PostMapping
    public Queue createQueue(@RequestBody Queue queue) { return queueService.createQueue(queue); }
    @GetMapping
    public List<Queue> getQueues() { return queueService.getAllQueues(); }
}
"@

    "worker\WorkerManager.java" = @"
package com.jobplatform.scheduler.worker;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WorkerManager {
    // Virtual Threads executor for job execution
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public void executeJob(Runnable jobTask) {
        executorService.submit(jobTask);
    }
}
"@

    "scheduler\JobSchedulerEngine.java" = @"
package com.jobplatform.scheduler.scheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobSchedulerEngine {
    
    @Scheduled(fixedRate = 5000)
    public void pollScheduledJobs() {
        // Here we would check the ScheduledJob table and push to Queue / Worker
        log.info("Polling scheduled jobs...");
    }
}
"@
}

foreach ($key in $files.Keys) {
    Set-Content -Path "$baseDir\$key" -Value $files[$key]
}
