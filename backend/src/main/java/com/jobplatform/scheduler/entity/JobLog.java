package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "job_logs")
public class JobLog extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_execution_id")
    private JobExecution jobExecution;
    private String logMessage;
    private String level;

    public JobExecution getJobExecution() { return this.jobExecution; }
    public void setJobExecution(JobExecution jobExecution) { this.jobExecution = jobExecution; }
    public String getLogMessage() { return this.logMessage; }
    public void setLogMessage(String logMessage) { this.logMessage = logMessage; }
    public String getLevel() { return this.level; }
    public void setLevel(String level) { this.level = level; }
}
