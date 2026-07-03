package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "worker_heartbeats")
public class WorkerHeartbeat extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;
    private LocalDateTime lastSeen;

    public Worker getWorker() { return this.worker; }
    public void setWorker(Worker worker) { this.worker = worker; }
    public LocalDateTime getLastSeen() { return this.lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
}
