# Design Decisions & Trade-offs

When designing this distributed job scheduling platform, several architectural and technical decisions were made to balance complexity, performance, and reliability. Here is a breakdown of the major trade-offs.

## 1. Database Polling vs. Event-Driven Execution

**Decision:** Implemented an Event-Driven architecture using Spring Application Events instead of relying solely on database polling for new jobs.
**Trade-off:** 
Traditional job schedulers often use a dedicated thread to poll the `jobs` table every few seconds (e.g., `SELECT * FROM jobs WHERE status = 'PENDING'`). While this guarantees that no jobs are missed, it introduces artificial latency (jobs wait up to X seconds to be picked up) and wastes database CPU cycles on empty queries.
By using Spring Events (`JobCreatedEvent`), the exact moment a job is saved via the API, the execution engine is notified and dispatches the job to the thread pool instantly. This drastically reduces latency and database load.

## 2. Concurrency: Java 21 Virtual Threads vs. Standard Thread Pools

**Decision:** Leveraged Java 21 Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`) for the Worker Manager.
**Trade-off:**
Standard OS-level thread pools (like `FixedThreadPool`) are heavy and limit the number of concurrent jobs you can run before running out of memory or thrashing the CPU. Virtual Threads, introduced in Java 21, are incredibly lightweight. We can spawn thousands of them concurrently for I/O bound tasks (like webhook calls or DB writes) without crashing the JVM. The trade-off is that Virtual Threads are not ideal for heavy CPU-bound algorithmic processing, but since job schedulers mostly orchestrate I/O tasks, it's the perfect fit.

## 3. Database Selection: Relational (PostgreSQL/H2) vs. NoSQL

**Decision:** Chose a strict relational schema using JPA/Hibernate over a NoSQL document store (like MongoDB).
**Trade-off:**
A job scheduler requires strict ACID guarantees. If a job is claimed by a worker, we must guarantee that a network partition won't cause a second worker to claim the exact same job. Relational databases support strong consistency and row-level locking. While NoSQL databases offer higher raw write throughput, they often sacrifice the strong consistency needed to prevent duplicate job executions.

## 4. Avoiding Complex "SKIP LOCKED" for the Prototype

**Decision:** Skipped native `SELECT ... FOR UPDATE SKIP LOCKED` queries in favor of optimistic/simple state transitions for the prototype phase.
**Trade-off:**
In a true multi-node cluster, workers need to atomically claim rows without blocking each other. The industry standard is `SKIP LOCKED`. However, writing native, database-specific SQL (since H2 and Postgres handle it slightly differently) adds significant complexity and hurts portability. For this scope, the event-driven approach ensures jobs are dispatched efficiently in a single-node setup. 

## 5. Frontend Architecture: Monolith vs. Microservices

**Decision:** Built the React frontend and Spring Boot backend in a single monolithic repository structure.
**Trade-off:**
Splitting the frontend and backend into separate repositories would fully decouple the deployment pipelines. However, keeping them in a single repository (monorepo style) makes it much easier to test locally and ensures that API changes in the backend are immediately reflected and tested against the frontend during development.
