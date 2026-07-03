package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "workers")
public class Worker extends BaseEntity {
    private String hostname;
    private String status;

    public String getHostname() { return this.hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }
}
