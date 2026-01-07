package contest_site.contest_site.controller;
import contest_site.contest_site.dto.CodeSubmission;
import contest_site.contest_site.model.Submission;
import contest_site.contest_site.service.SubmissionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/run")
@AllArgsConstructor
public class SandboxController {

    private final SubmissionService submissionService;

    @PostMapping
    public String execute(@RequestBody CodeSubmission submission) {
        try {
            Submission sub=submissionService.submitCode(submission);
            return sub.getVerdict();
        } catch (Exception e){
            System.out.println("Controller side error: " + e.getMessage());
        }
        return "Controller side error";
    }
}