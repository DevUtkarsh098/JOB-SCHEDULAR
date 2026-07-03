package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {
    private String name;
    @OneToMany(mappedBy = "organization")
    private List<Project> projects;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public List<Project> getProjects() { return this.projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
}
