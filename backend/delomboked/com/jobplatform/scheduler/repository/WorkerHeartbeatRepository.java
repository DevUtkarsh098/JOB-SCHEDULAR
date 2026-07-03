package com.jobplatform.scheduler.repository;
import com.jobplatform.scheduler.entity.WorkerHeartbeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerHeartbeatRepository extends JpaRepository<WorkerHeartbeat, Long> {
}
