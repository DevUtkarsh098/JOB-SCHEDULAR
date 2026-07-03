package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {
    private String name;
    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public Organization getOrganization() { return this.organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }
}
