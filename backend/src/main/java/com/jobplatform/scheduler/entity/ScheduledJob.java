package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "scheduled_jobs")
public class ScheduledJob extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    private String cronExpression;

    public Job getJob() { return this.job; }
    public void setJob(Job job) { this.job = job; }
    public String getCronExpression() { return this.cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
}
