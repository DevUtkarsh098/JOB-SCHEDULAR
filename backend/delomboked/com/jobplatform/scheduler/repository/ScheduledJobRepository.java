package com.jobplatform.scheduler.repository;
import com.jobplatform.scheduler.entity.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
}
