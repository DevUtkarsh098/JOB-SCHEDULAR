package com.jobplatform.scheduler.service;

import com.jobplatform.scheduler.entity.Queue;
import com.jobplatform.scheduler.repository.QueueRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QueueService {
    private final QueueRepository queueRepository;

    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    public Queue createQueue(Queue queue) { return queueRepository.save(queue); }
    public List<Queue> getAllQueues() { return queueRepository.findAll(); }
}
