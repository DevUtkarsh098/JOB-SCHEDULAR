package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_executions")
public class JobExecution extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public Job getJob() { return this.job; }
    public void setJob(Job job) { this.job = job; }
    public Worker getWorker() { return this.worker; }
    public void setWorker(Worker worker) { this.worker = worker; }
    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartedAt() { return this.startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return this.completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
