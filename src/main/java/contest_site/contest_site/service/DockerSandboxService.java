package contest_site.contest_site.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DockerSandboxService {
    // 1. SETUP: Folder on your HOST machine where code lives
    // Docker requires ABSOLUTE paths for volume mounting
    private static final String HOST_CODE_DIR = System.getProperty("user.dir") + "/temp_code_folder";
    private static final String DOCKER_IMAGE = "openjdk:27-ea-trixie"; // Lightweight Java image

    public String runCode(String userCode) throws IOException, InterruptedException {
        // Create the directory if it doesn't exist
        File dir = new File(HOST_CODE_DIR);
        if (!dir.exists()) dir.mkdirs();

        // Save user code to "Main.java" locally
        Path filePath = Paths.get(HOST_CODE_DIR, "Main.java");
        Files.write(filePath, userCode.getBytes());

        // --- STEP 1: COMPILE (in Docker) ---
        // Command: docker run --rm -v {host_path}:/app -w /app openjdk:17 javac Main.java
        ProcessBuilder compileProc = new ProcessBuilder(
                "docker", "run",
                "--rm",                        // Delete container after running
                "-v", HOST_CODE_DIR + ":/app", // Mount our local folder to /app inside container
                "-w", "/app",                  // Set working directory to /app
                DOCKER_IMAGE,                  // Image to use
                "javac", "Main.java"           // Command to run
        );

        Process compiler = compileProc.start();
        if (!compiler.waitFor(10, TimeUnit.SECONDS)) { // Give it more time (Docker is slower)
            compiler.destroy();
            return "Error: Compilation timed out (Docker startup might be slow)";
        }

        if (compiler.exitValue() != 0) {
            String error = readStream(compiler.getErrorStream());
            return "Compilation Failed:\n" + error;
        }

        // --- STEP 2: EXECUTE (in Docker) ---
        // Command: docker run --rm -v {host_path}:/app -w /app openjdk:17 java Main
        ProcessBuilder runProc = new ProcessBuilder(
                "docker", "run",
                "--rm",
                "-v", HOST_CODE_DIR + ":/app",
                "-w", "/app",
                DOCKER_IMAGE,
                "java", "Main"
        );

        Process runner = runProc.start();

        // Wait for execution
        if (!runner.waitFor(5, TimeUnit.SECONDS)) {
            runner.destroy();
            return "Error: Time Limit Exceeded";
        }


        String output = readStream(runner.getInputStream());
        String error = readStream(runner.getErrorStream());

        return error.isEmpty() ? output : "Runtime Error:\n" + error;
    }

    // Helper method to read input streams
    private String readStream(InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream))
                .lines().collect(Collectors.joining("\n"));
    }
}
