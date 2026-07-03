$baseDir = "c:\JAVA PROJECT\Codity\backend\src\main\java\com\jobplatform\scheduler"
$entityDir = "$baseDir\entity"
$repoDir = "$baseDir\repository"

$entities = @{
    "Organization.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "organizations")
@Getter @Setter
public class Organization extends BaseEntity {
    private String name;
    @OneToMany(mappedBy = "organization")
    private List<Project> projects;
}
"@

    "Project.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter @Setter
public class Project extends BaseEntity {
    private String name;
    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;
}
"@

    "Queue.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "queues")
@Getter @Setter
public class Queue extends BaseEntity {
    private String name;
    private String type;
    private Integer concurrency;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
"@

    "RetryPolicy.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "retry_policies")
@Getter @Setter
public class RetryPolicy extends BaseEntity {
    private String name;
    private String strategy;
    private Integer maxRetries;
    private Integer backoffMultiplier;
}
"@

    "Job.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "jobs")
@Getter @Setter
public class Job extends BaseEntity {
    private String name;
    private String payload;
    private String status;
    @ManyToOne
    @JoinColumn(name = "queue_id")
    private Queue queue;
    @ManyToOne
    @JoinColumn(name = "retry_policy_id")
    private RetryPolicy retryPolicy;
}
"@

    "Worker.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workers")
@Getter @Setter
public class Worker extends BaseEntity {
    private String hostname;
    private String status;
}
"@

    "WorkerHeartbeat.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "worker_heartbeats")
@Getter @Setter
public class WorkerHeartbeat extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;
    private LocalDateTime lastSeen;
}
"@

    "JobExecution.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_executions")
@Getter @Setter
public class JobExecution extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
"@

    "JobLog.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "job_logs")
@Getter @Setter
public class JobLog extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_execution_id")
    private JobExecution jobExecution;
    private String logMessage;
    private String level;
}
"@

    "DeadLetterQueue.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dead_letter_queue")
@Getter @Setter
public class DeadLetterQueue extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    private String failureReason;
}
"@

    "ScheduledJob.java" = @"
package com.jobplatform.scheduler.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheduled_jobs")
@Getter @Setter
public class ScheduledJob extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    private String cronExpression;
}
"@

}

foreach ($key in $entities.Keys) {
    Set-Content -Path "$entityDir\$key" -Value $entities[$key]
}

$repos = @("User", "Organization", "Project", "Queue", "Job", "RetryPolicy", "Worker", "WorkerHeartbeat", "JobExecution", "JobLog", "DeadLetterQueue", "ScheduledJob")

foreach ($repo in $repos) {
    $content = @"
package com.jobplatform.scheduler.repository;
import com.jobplatform.scheduler.entity.$repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${repo}Repository extends JpaRepository<$repo, Long> {
}
"@
    Set-Content -Path "$repoDir\${repo}Repository.java" -Value $content
}
