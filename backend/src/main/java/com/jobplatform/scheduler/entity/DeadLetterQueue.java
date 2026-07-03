package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "dead_letter_queue")
public class DeadLetterQueue extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    private String failureReason;

    public Job getJob() { return this.job; }
    public void setJob(Job job) { this.job = job; }
    public String getFailureReason() { return this.failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
