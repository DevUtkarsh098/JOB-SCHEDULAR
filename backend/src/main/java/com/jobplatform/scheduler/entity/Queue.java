package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "queues")
public class Queue extends BaseEntity {
    private String name;
    private String type;
    private Integer concurrency;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }
    public Integer getConcurrency() { return this.concurrency; }
    public void setConcurrency(Integer concurrency) { this.concurrency = concurrency; }
    public Project getProject() { return this.project; }
    public void setProject(Project project) { this.project = project; }
}
