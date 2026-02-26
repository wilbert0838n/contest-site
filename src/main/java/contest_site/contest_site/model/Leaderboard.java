package contest_site.contest_site.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "leaderboard", indexes = {
        @Index(name = "idx_contest_ranking", columnList = "contest_id, score DESC, penalty_time ASC")
})
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = false)
    private Long contestId;

    // Still relates to your actual User table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score = 0;

    @Column( nullable = false)
    private Integer penaltyTime = 0;

    @Column(nullable = false)
    private Integer problemsSolved = 0;

    private LocalDateTime lastUpdated;
}