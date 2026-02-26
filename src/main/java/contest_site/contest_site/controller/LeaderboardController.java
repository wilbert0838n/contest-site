package contest_site.contest_site.controller;
import contest_site.contest_site.dto.LeaderboardDTO;
import contest_site.contest_site.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardDTO>> getLeaderboard(
            @RequestParam(required = false) Long contestId,
            @RequestParam(defaultValue = "100") int limit) {

        if (contestId != null) {
            List<LeaderboardDTO> weeklyBoard = leaderboardService.getWeeklyLeaderboard(contestId, limit);
            return ResponseEntity.ok(weeklyBoard);
        }

        List<LeaderboardDTO> globalBoard = leaderboardService.getGlobalLeaderboard(limit);
        return ResponseEntity.ok(globalBoard);
    }
}
