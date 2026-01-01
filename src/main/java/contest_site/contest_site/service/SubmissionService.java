package contest_site.contest_site.service;

import contest_site.contest_site.dto.CodeSubmission;
import contest_site.contest_site.model.Problem;
import contest_site.contest_site.model.Submission;
import contest_site.contest_site.model.User;
import contest_site.contest_site.repo.ProblemRepository;
import contest_site.contest_site.repo.SubmissionRepository;
import contest_site.contest_site.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final CodeRunnerService codeRunnerService;

    public Submission submitCode(CodeSubmission dto) throws IOException, InterruptedException {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(()->new RuntimeException("User not found"));
        Problem problem = problemRepository.findById(dto.getProblemId())
                .orElseThrow(()->new RuntimeException("Problem not found"));

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setCode(dto.getCode());
        submission.setLanguage(dto.getLanguage());
        submission.setVerdict("PENDING");
        Submission saved = submissionRepository.save(submission);

        codeRunnerService.runCode(saved);

        return saved;
    }
}
