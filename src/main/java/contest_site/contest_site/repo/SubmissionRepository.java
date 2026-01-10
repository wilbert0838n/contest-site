package contest_site.contest_site.repo;

import contest_site.contest_site.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByProblemIdAndVerdict(Long problemId, String verdict);
    Integer countByProblemIdAndVerdict(Long problemId, String verdict);
    Integer countByProblemId(Long  problemId);
}