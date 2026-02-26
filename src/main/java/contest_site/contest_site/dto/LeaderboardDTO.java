package contest_site.contest_site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardDTO {
    private int rank;
    private String username;
    private int score;
    private int penaltyTime;
    private int problemsSolved;
}