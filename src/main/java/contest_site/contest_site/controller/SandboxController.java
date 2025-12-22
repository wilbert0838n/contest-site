package contest_site.contest_site.controller;
import contest_site.contest_site.model.CodeSubmission;
import contest_site.contest_site.service.DockerSandboxService;
import contest_site.contest_site.service.PooledSandboxService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sandbox")
public class SandboxController {

    private final PooledSandboxService sandboxService;

    public SandboxController(PooledSandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }

    @PostMapping("/run")
    public String execute(@RequestBody CodeSubmission submission) {
        try {
            return sandboxService.runCode(submission.getCode());
        } catch (Exception e) {
            return "Server Error: " + e.getMessage();
        }
    }
}