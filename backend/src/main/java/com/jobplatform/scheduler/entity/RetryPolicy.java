package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "retry_policies")
public class RetryPolicy extends BaseEntity {
    private String name;
    private String strategy;
    private Integer maxRetries;
    private Integer backoffMultiplier;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public String getStrategy() { return this.strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
    public Integer getMaxRetries() { return this.maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    public Integer getBackoffMultiplier() { return this.backoffMultiplier; }
    public void setBackoffMultiplier(Integer backoffMultiplier) { this.backoffMultiplier = backoffMultiplier; }
}
