package contest_site.contest_site.service;
import contest_site.contest_site.model.Submission;
import contest_site.contest_site.model.TestCase;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Primary
@Service
@AllArgsConstructor
public class PooledSandboxService implements CodeRunnerService {
    private final ContainerPoolManager poolManager;

    private static final String TEMP_DIR = System.getProperty("user.dir") + "/temp_code_folder";

    private String readStream(InputStream stream) throws IOException {
        return new String(stream.readAllBytes()).trim();
    }

    private void runCommand(String... command) throws IOException, InterruptedException {
        Process process=Runtime.getRuntime().exec(command);
        process.waitFor();
    }

    @Transactional //auto saves when dirtied
    public void runCode(Submission submission) throws IOException, InterruptedException {
        Language lang = submission.getLanguage();
        String containerId = poolManager.getContainer(lang);

        // Setup local file
        String uniqueFileName = "Main_" + UUID.randomUUID() + "."+lang.toString().toLowerCase();
        Path localPath = Paths.get(TEMP_DIR, uniqueFileName);
        new File(TEMP_DIR).mkdirs();
        try {
            Files.write(localPath,submission.getCode().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {

            //Copy code into container
            // Command: docker cp local/Main_123.java containerId:/app/Main.java
            runCommand("docker", "exec", containerId, "mkdir", "-p", "/app"); // Ensure /app exists
            runCommand("docker", "cp", localPath.toString(), containerId + ":/app/"+lang.getFileName());

            //Compilation Process Starts Here
            String[] compileCommand= lang.getCompileCommand(containerId);
            if(compileCommand!=null){
                Process compileProc =  Runtime.getRuntime().exec(compileCommand);

                if (!compileProc.waitFor(10, TimeUnit.SECONDS)){
                    submission.setVerdict("Compile Time exceeded");
                    return;
                }

                if (compileProc.exitValue() != 0){
                    submission.setVerdict("Compilation Failed");
                    String errorMsg=readStream(compileProc.getErrorStream());
                    System.out.println(errorMsg);
                    return;
                }
            }
            //Compilation Process Ends Here

            //Execution Process Starts Here
            String[] runCommand= lang.getRunCommand(containerId);
            List<TestCase> testCases=submission.getProblem().getTestCases();

            for(TestCase testCase:testCases){

                Process runProc = Runtime.getRuntime().exec(runCommand);

                try (OutputStream stdin = runProc.getOutputStream()) {
                    // Write the input bytes to the Docker process
                    stdin.write(testCase.getInputContent().getBytes());
                    stdin.flush();
                    // Closing the stream sends EOF (End of File), telling the program "Input is finished"
                } catch (IOException e) {
                    System.out.println("Error while inputting testcase: "+e.getMessage());
                    submission.setVerdict("Error while inputting testcase");
                    return;
                }

                if(!runProc.waitFor(5, TimeUnit.SECONDS)){
                    submission.setVerdict("Time Limit Exceeded");
                    return;
                }

                //store output and error
                String output = readStream(runProc.getInputStream());
                String error = readStream(runProc.getErrorStream());
                if(error.isEmpty()){
                    if(!output.equals(testCase.getOutputContent())){
                        submission.setVerdict("Wrong Answer");
                        return;
                    }
                }
                else{
                    System.out.println("Runtime error: "+error);
                    submission.setVerdict("Runtime Error");
                    return;
                }
            }

            submission.setVerdict("All Testcase Passed");
            //Execution Process Ends Here

        } finally {
            // We destroy the container because it's now dirty
            // The PoolManager has already started creating a replacement in the background
            Runtime.getRuntime().exec(new String[]{"docker", "rm", "-f", containerId});
            // Delete local temp file
            Files.deleteIfExists(localPath);
        }
    }

}
