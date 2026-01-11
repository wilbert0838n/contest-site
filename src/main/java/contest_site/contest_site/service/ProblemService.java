package contest_site.contest_site.service;

import contest_site.contest_site.dto.ExampleDTO;
import contest_site.contest_site.dto.ProblemDTO;
import contest_site.contest_site.model.Example;
import contest_site.contest_site.model.Problem;
import contest_site.contest_site.repo.ProblemRepository;
import contest_site.contest_site.repo.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;

    public ProblemDTO getProblemById(Long problemId) {
        Problem problem = problemRepository.findById(problemId).orElse(null);
        return modelToDTO(problem);
    }

    public List<ProblemDTO> getProblemsByContestId(long contestId) {
        try{
            List<Problem> problems = problemRepository.findByContestId(contestId);
            List<ProblemDTO> problemDTOS = new ArrayList<>();

            for (Problem problem : problems) {
                problemDTOS.add(modelToDTO(problem));
            }
            return problemDTOS;

        }catch(Exception e){
            System.out.println("Contest does not exist, error msg: "+e.getMessage());
            return new ArrayList<>();
        }
    }

    public ProblemDTO modelToDTO(Problem problem) {
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setId(problem.getId());
        problemDTO.setProblemName(problem.getProblemName());
        problemDTO.setDifficulty(problem.getDifficulty());
        problemDTO.setDescription(problem.getDescription());
        problemDTO.setInputFormat(problem.getInputFormat());
        problemDTO.setOutputFormat(problem.getOutputFormat());
        problemDTO.setTimeLimit(problem.getTimeLimit());
        problemDTO.setSpaceLimit(problem.getSpaceLimit());

        Integer totalSubmissions = submissionRepository.countByProblemId(problem.getId());
        Integer correctSubmissions = submissionRepository.countByProblemIdAndVerdict(problem.getId(),"All Testcase Passed");
        if (totalSubmissions > 0) {
            double accuracy = ((double) correctSubmissions / totalSubmissions) * 100.0;
            accuracy = Math.round(accuracy * 100.0) / 100.0;

            problemDTO.setAccuracy(accuracy);
        } else {
            problemDTO.setAccuracy(0.0);
        }

        problemDTO.setTotal(totalSubmissions);
        problemDTO.setSolved(correctSubmissions);

        List<ExampleDTO> exampleDTOS = new ArrayList<>();
        for(Example example : problem.getExamples()){
            ExampleDTO exampleDTO = new ExampleDTO();
            exampleDTO.setId(exampleDTO.getId());
            exampleDTO.setInput(example.getInput());
            exampleDTO.setOutput(example.getOutput());
            exampleDTO.setExplanation(example.getExplanation());
            exampleDTOS.add(exampleDTO);
        }
        problemDTO.setExamples(exampleDTOS);
        
        return problemDTO;
    }


}
