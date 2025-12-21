package contest_site.contest_site.controller;
import contest_site.contest_site.model.CodeSubmission;
import contest_site.contest_site.service.DockerSandboxService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sandbox")
public class SandboxController {

    // CHANGE 1: Change the type from SimpleSandboxService to DockerSandboxService
    private final DockerSandboxService sandboxService;

    // CHANGE 2: Update the constructor to inject the Docker service
    public SandboxController(DockerSandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }

    @PostMapping("/run")
    public String execute(@RequestBody CodeSubmission submission) {
        try {
            // CHANGE 3: Nothing!
            // Since both services have a "runCode" method with the same signature,
            // this line stays exactly the same.
            return sandboxService.runCode(submission.getCode());
        } catch (Exception e) {
            return "Server Error: " + e.getMessage();
        }
    }
}