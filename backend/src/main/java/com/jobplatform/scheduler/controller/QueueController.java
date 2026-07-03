package com.jobplatform.scheduler.controller;

import com.jobplatform.scheduler.entity.Queue;
import com.jobplatform.scheduler.service.QueueService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/queues")
public class QueueController {
    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    public Queue createQueue(@RequestBody Queue queue) { return queueService.createQueue(queue); }
    
    @GetMapping
    public List<Queue> getQueues() { return queueService.getAllQueues(); }
}
