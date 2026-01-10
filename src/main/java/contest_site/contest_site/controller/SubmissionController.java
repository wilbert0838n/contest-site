package contest_site.contest_site.controller;

import contest_site.contest_site.dto.SubmissionDTO;
import contest_site.contest_site.model.Submission;
import contest_site.contest_site.repo.SubmissionRepository;
import contest_site.contest_site.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository; // to change, via service layer

    @GetMapping("/problem/{problemId}/successful")
    public ResponseEntity<List<SubmissionDTO>> getSuccessfulSubmissions(@PathVariable Long problemId) {
        return ResponseEntity.ok(submissionService.getHackableSubmissions(problemId));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<SubmissionDTO> getSubmissionById(@PathVariable Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        return ResponseEntity.ok(submissionService.modelToDTO(submission));
    }
}
