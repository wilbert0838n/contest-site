package contest_site.contest_site.repo;

import contest_site.contest_site.model.Leaderboard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<Leaderboard> findByContestIdOrderByScoreDescPenaltyTimeAsc(Long contestId, Pageable pageable);

    Optional<Leaderboard> findByContestIdAndUserId(Long contestId, Long userId);
}
