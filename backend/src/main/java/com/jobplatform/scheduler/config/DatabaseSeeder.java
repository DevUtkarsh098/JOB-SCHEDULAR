package com.jobplatform.scheduler.config;

import com.jobplatform.scheduler.entity.*;
import com.jobplatform.scheduler.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final OrganizationRepository orgRepo;
    private final ProjectRepository projRepo;
    private final QueueRepository queueRepo;
    private final UserRepository userRepo;

    public DatabaseSeeder(OrganizationRepository orgRepo, ProjectRepository projRepo, QueueRepository queueRepo, UserRepository userRepo) {
        this.orgRepo = orgRepo;
        this.projRepo = projRepo;
        this.queueRepo = queueRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() == 0) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@codity.com");
            user.setRole("ADMIN");
            userRepo.save(user);

            Organization org = new Organization();
            org.setName("Codity Enterprise");
            orgRepo.save(org);

            Project proj = new Project();
            proj.setName("Main Platform");
            proj.setOrganization(org);
            projRepo.save(proj);

            Queue queue = new Queue();
            queue.setName("default-queue");
            queue.setType("FIFO");
            queue.setConcurrency(5);
            queue.setProject(proj);
            queueRepo.save(queue);
            
            Queue emailQueue = new Queue();
            emailQueue.setName("email-notifications");
            emailQueue.setType("PRIORITY");
            emailQueue.setConcurrency(2);
            emailQueue.setProject(proj);
            queueRepo.save(emailQueue);
        }
    }
}
