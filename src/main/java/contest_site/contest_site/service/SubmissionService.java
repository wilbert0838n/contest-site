package contest_site.contest_site.service;

import contest_site.contest_site.dto.CodeSubmission;
import contest_site.contest_site.dto.SubmissionDTO;
import contest_site.contest_site.model.Problem;
import contest_site.contest_site.model.Submission;
import contest_site.contest_site.model.User;
import contest_site.contest_site.repo.ProblemRepository;
import contest_site.contest_site.repo.SubmissionRepository;
import contest_site.contest_site.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        submission.setVerdict("Pending");
        Submission saved = submissionRepository.save(submission);

        codeRunnerService.runCode(saved);

        return saved;
    }


    public SubmissionDTO modelToDTO(Submission submission) {
        SubmissionDTO dto = new SubmissionDTO();

        dto.setId(submission.getId());
        dto.setUsername(submission.getUser().getUsername());
        dto.setProblemId(submission.getProblem().getId());
        dto.setCode(submission.getCode());
        dto.setVerdict(submission.getVerdict());
        dto.setLanguage(submission.getLanguage().toString());
        dto.setExecutionTime(submission.getExecutionTime() + "ms");
        dto.setMemoryUsed(submission.getMemoryUsed() + "KB");
        dto.setSubmittedAt(submission.getSubmittedAt().toString());
        return dto;
    }

    public List<SubmissionDTO> getHackableSubmissions(Long problemId) {
        List<Submission> submissions =
                submissionRepository.findByProblemIdAndVerdict(problemId, "All Testcase Passed");

        return submissions.stream()
                .map(sub -> modelToDTO(sub))
                .collect(Collectors.toList());
    }

}
