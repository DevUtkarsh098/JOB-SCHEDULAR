package com.jobplatform.scheduler.service;

import com.jobplatform.scheduler.entity.Job;
import com.jobplatform.scheduler.repository.JobRepository;
import com.jobplatform.scheduler.event.JobCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobService = new JobService(jobRepository, eventPublisher);
    }

    @Test
    void testCreateJobPublishesEvent() {
        // Arrange
        Job job = new Job();
        job.setName("Test Job");
        job.setStatus("PENDING");

        Job savedJob = new Job();
        savedJob.setId(1L);
        savedJob.setName("Test Job");
        savedJob.setStatus("PENDING");

        when(jobRepository.save(any(Job.class))).thenReturn(savedJob);

        // Act
        Job result = jobService.createJob(job);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        
        // Verify event was published
        ArgumentCaptor<JobCreatedEvent> eventCaptor = ArgumentCaptor.forClass(JobCreatedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        
        JobCreatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(1L, publishedEvent.getJob().getId());
    }
}
