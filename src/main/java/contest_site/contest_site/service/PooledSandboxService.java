package contest_site.contest_site.service;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Primary
@Service
public class PooledSandboxService implements CodeRunnerService {
    private final ContainerPoolManager poolManager;
    private static final String TEMP_DIR = System.getProperty("user.dir") + "/temp_code_folder";

    public PooledSandboxService(ContainerPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    private String readStream(InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream))
                .lines().collect(Collectors.joining("\n"));
    }

    private void runCommand(String... command) throws IOException, InterruptedException {
        new ProcessBuilder(command).start().waitFor();
    }

    public String runCode(String userCode) throws IOException, InterruptedException {
        // 1. Get a fresh container from the pool (Fast!)
        String containerId = poolManager.getContainer();

        // Setup local file
        String uniqueFileName = "Main_" + UUID.randomUUID() + ".java";
        Path localPath = Paths.get(TEMP_DIR, uniqueFileName);
        // Ensure dir exists
        new File(TEMP_DIR).mkdirs();
        Files.write(localPath, userCode.getBytes());

        try {
            // 2. COPY code into container
            // Command: docker cp local/Main_123.java containerId:/app/Main.java
            runCommand("docker", "exec", containerId, "mkdir", "-p", "/app"); // Ensure /app exists
            runCommand("docker", "cp", localPath.toString(), containerId + ":/app/Main.java");

            // 3. COMPILE
            String[] compileCmd = {"docker","exec",containerId,"javac", "/app/Main.java"};
            Process compileProc =  Runtime.getRuntime().exec(compileCmd);
            if (!compileProc.waitFor(10, TimeUnit.SECONDS)) return "Time Limit Exceeded (Compilation)";
            if (compileProc.exitValue() != 0){
                return "Compilation Failed";
            }

            // 4. EXECUTE
            String[] runCmd = {"docker", "exec", "-i",containerId, "java", "-cp", "/app","Main"};
            Process runProc = Runtime.getRuntime().exec(runCmd);

            // --- PROVIDE INPUT HERE ---
            String userInput = "10"; // Example input: "10 20"
            try (OutputStream stdin = runProc.getOutputStream()) {
                // Write the input bytes to the Docker process
                stdin.write(userInput.getBytes());
                stdin.flush();
                // Closing the stream sends EOF (End of File), telling the program "Input is finished"
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!runProc.waitFor(5, TimeUnit.SECONDS))
                return "Time Limit Exceeded (Runtime)";

            // Capture Output
            String output = readStream(runProc.getInputStream());
            String error = readStream(runProc.getErrorStream());
            return error.isEmpty() ? output : error;

        } finally {
            // 5. CLEANUP (CRITICAL)
            // We destroy the container because it's now "dirty"
            // The PoolManager has already started creating a replacement in the background
            new ProcessBuilder("docker", "rm", "-f", containerId).start();

            // Delete local temp file
            Files.deleteIfExists(localPath);
        }
    }


}
