package contest_site.contest_site.config;

import contest_site.contest_site.model.Problem;
import contest_site.contest_site.model.TestCase;
import contest_site.contest_site.repo.ProblemRepository;
import contest_site.contest_site.repo.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProblemSeeder implements CommandLineRunner {
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only seed if the database is empty
        if (problemRepository.count() == 0) {
            System.out.println("--- Seeding Database with Sample Problem ---");


            Problem p = new Problem();
            p.setProblemName("Absolute Difference");
            p.setDescription("Given two integers A and B, find the absolute difference |A - B|.");
            p.setDifficulty("Easy");
            p.setContestId(1L);
            p.setInputFormat("Two integers A and B separated by a space.");
            p.setOutputFormat("A single integer representing the absolute difference.");
            p.setMultipleSolutionAllowed(false);
            p.setTimeLimit(1.0);
            p.setSpaceLimit(256);

            p = problemRepository.save(p);

            TestCase tc1 = new TestCase();
            tc1.setProblem(p);
            tc1.setInputContent("10 4");
            tc1.setOutputContent("6");

            TestCase tc2 = new TestCase();
            tc2.setProblem(p);
            tc2.setInputContent("4 10");
            tc2.setOutputContent("6");

            testCaseRepository.save(tc1);
            testCaseRepository.save(tc2);

            System.out.println("--- Database Seeded Successfully ---");
        }
    }
}