package com.jobplatform.scheduler.worker;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WorkerManager {
    // Virtual Threads executor for job execution
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public void executeJob(Runnable jobTask) {
        executorService.submit(jobTask);
    }
}
