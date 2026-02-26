package contest_site.contest_site.service;

import contest_site.contest_site.dto.LeaderboardDTO;
import contest_site.contest_site.model.Leaderboard;
import contest_site.contest_site.model.User;
import contest_site.contest_site.repo.LeaderboardRepository;
import contest_site.contest_site.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepo;
    private final UserRepository userRepository;

    public List<LeaderboardDTO> getWeeklyLeaderboard(Long contestId, int limit) {
        Pageable topN = PageRequest.of(0, limit);
        List<Leaderboard> topEntries = leaderboardRepo
                .findByContestIdOrderByScoreDescPenaltyTimeAsc(contestId, topN);

        List<LeaderboardDTO> response = new ArrayList<>();
        int currentRank = 1;

        for (Leaderboard entry : topEntries) {
            response.add(new LeaderboardDTO(
                    currentRank++,
                    entry.getUser().getUsername(),
                    entry.getScore(),
                    entry.getPenaltyTime(),
                    entry.getProblemsSolved()
            ));
        }

        return response;
    }

    public List<LeaderboardDTO> getGlobalLeaderboard(int limit) {
        Pageable topN = PageRequest.of(0, limit);
        List<User> topUsers = userRepository.findByOrderByRatingDesc(topN);

        List<LeaderboardDTO> response = new ArrayList<>();
        int currentRank = 1;

        for (User user : topUsers) {
            response.add(new LeaderboardDTO(
                    currentRank++,
                    user.getUsername(),
                    user.getRating() != null ? user.getRating() : 1200,
                    0,
                    user.getTotalProblemsSolved() != null ? user.getTotalProblemsSolved() : 0
            ));
        }

        return response;
    }

    @Transactional
    public void updateContestScore(Long contestId, Long userId, int pointsEarned, int penaltyMinutes, boolean isNewProblemSolved) {
        Leaderboard entry = leaderboardRepo.findByContestIdAndUserId(contestId, userId)
                .orElseGet(() -> {
                    Leaderboard newEntry = new Leaderboard();
                    newEntry.setContestId(contestId);

                    User userReference = userRepository.getReferenceById(userId);
                    newEntry.setUser(userReference);

                    newEntry.setScore(0);
                    newEntry.setPenaltyTime(0);
                    newEntry.setProblemsSolved(0);
                    return newEntry;
                });


        entry.setScore(entry.getScore() + pointsEarned);
        entry.setPenaltyTime(entry.getPenaltyTime() + penaltyMinutes);

        if (isNewProblemSolved) {
            entry.setProblemsSolved(entry.getProblemsSolved() + 1);
        }

        entry.setLastUpdated(LocalDateTime.now());
        leaderboardRepo.save(entry);
    }
}
