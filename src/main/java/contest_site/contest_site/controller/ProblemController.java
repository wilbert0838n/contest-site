package contest_site.contest_site.controller;
import contest_site.contest_site.dto.ProblemDTO;
import contest_site.contest_site.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping(params = "problemId")
    public ProblemDTO getProblemById(@RequestParam long problemId) {
        try{
            return problemService.getProblemById(problemId);
        }catch(Exception e){
            System.out.println("Error message: " + e.getMessage());
            return null;
        }
    }

    @GetMapping(params = "contestId")
    public List<ProblemDTO> getProblemsByContestId(@RequestParam long contestId) {
        try{
            return problemService.getProblemsByContestId(contestId);
        }catch(Exception e){
            System.out.println("Error message: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}
