package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {
    private String name;
    private String payload;
    private String status;
    @ManyToOne
    @JoinColumn(name = "queue_id")
    private Queue queue;
    @ManyToOne
    @JoinColumn(name = "retry_policy_id")
    private RetryPolicy retryPolicy;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public String getPayload() { return this.payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }
    public Queue getQueue() { return this.queue; }
    public void setQueue(Queue queue) { this.queue = queue; }
    public RetryPolicy getRetryPolicy() { return this.retryPolicy; }
    public void setRetryPolicy(RetryPolicy retryPolicy) { this.retryPolicy = retryPolicy; }
}
