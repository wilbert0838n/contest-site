package contest_site.contest_site.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ContainerPoolManager {

    private final Map<Language,BlockingQueue<String>> containerPools = new ConcurrentHashMap<>();

    @PostConstruct //after spring boot initializes, start filling pool
    public void initializePool() {

        for(Language lang : Language.values()) {
            containerPools.put(
                    lang,
                    new LinkedBlockingQueue<>(lang.getPoolSize())
            );

            System.out.println("Filling "+lang+" Container Pool");
            for (int i = 0; i < lang.getPoolSize(); i++) {
                createNewContainerAsync(lang);
            }
        }
    }

    public String getContainer(Language language) {
        try {
            String containerId = containerPools.get(language).take(); // .take() waits if queue is empty
            CompletableFuture.runAsync(() -> {
                createNewContainerAsync(language);
            });
            return containerId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error getting container from pool", e);
        }
    }

    private void createNewContainerAsync(Language language) {

            try {
                String containerId = createContainer(language);
                containerPools.get(language).put(containerId);
                System.out.println(language.toString()+" Pool Refilled. Available: " +
                        containerPools.get(language).size());

            } catch (Exception e) {
                System.out.println("Error in Async container creation: "+e.getMessage());
            }

    }

    private String createContainer(Language lang) throws IOException {
        String containerName = "sandbox-" + UUID.randomUUID().toString();

        // invoke the container and keep it alive
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run",
                "-d",
                "--name", containerName,
                lang.getDockerImage(),
                "tail", "-f", "/dev/null"
        );

        Process p = pb.start();
        String containerId = new String(p.getInputStream().readAllBytes()).trim();
        return containerName;
    }


    @PreDestroy
    public void cleanup() {
        for(Language lang : Language.values()) {
            System.out.println("Deleting containers in pool of language "+lang);
            for (String id : containerPools.get(lang)) {
                try {
                    new ProcessBuilder("docker", "rm", "-f", id).start();
                } catch (IOException e) {
                    System.out.println("Error cleaning up container: "+e.getMessage());
                }
            }
        }
    }
}
