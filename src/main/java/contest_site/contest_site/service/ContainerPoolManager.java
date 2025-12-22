package contest_site.contest_site.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ContainerPoolManager {

    private static final int POOL_SIZE = 5; // Keep 5 containers ready
    private static final String IMAGE = "eclipse-temurin:17-jdk-alpine";

    // A thread-safe Queue holding ID Strings of running containers
    private final BlockingQueue<String> availableContainers = new LinkedBlockingQueue<>(POOL_SIZE);

    // 1. STARTUP: Fill the pool when Spring Boot starts
    @PostConstruct
    public void initializePool() {
        System.out.println("--- Initializing Container Pool ---");
        for (int i = 0; i < POOL_SIZE; i++) {
            createNewContainerAsync();
        }
    }

    // 2. GET: Service calls this to get a container ID
    public String getContainer() {
        try {
            // .take() waits if queue is empty (Thread Safe)
            String containerId = availableContainers.take();
            // Trigger a refill in the background immediately
            createNewContainerAsync();
            return containerId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error getting container from pool", e);
        }
    }

    // 3. REFILL: Creates a new container in the background
    private void createNewContainerAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                String containerId = createContainer();
                availableContainers.put(containerId); // Add to queue
                System.out.println("Pool Refilled. Available: " + availableContainers.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // The actual Docker Command to start a generic "sleeping" container
    private String createContainer() throws IOException {
        String containerName = "sandbox-" + UUID.randomUUID().toString();

        // Command: docker run -d --name uuid eclipse-temurin tail -f /dev/null
        // "tail -f /dev/null" keeps the container running forever doing nothing
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run",
                "-d",                      // Detached (background)
                "--name", containerName,   // Unique Name
                IMAGE,
                "tail", "-f", "/dev/null"  // Keep alive command
        );

        Process p = pb.start();
        // Read the Container ID (Docker prints it to stdout)
        String containerId = new String(p.getInputStream().readAllBytes()).trim();
        return containerName; // We return the name to refer to it later
    }

    // 4. SHUTDOWN: Cleanup all containers when Server stops
    @PreDestroy
    public void cleanup() {
        System.out.println("--- Killing all Pooled Containers ---");
        for (String id : availableContainers) {
            try {
                new ProcessBuilder("docker", "rm", "-f", id).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
