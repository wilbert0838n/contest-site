package contest_site.contest_site.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String problemName;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String inputFormat;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String outputFormat;

    @Column(nullable = false)
    private boolean isMultipleSolutionAllowed;

    @Column(columnDefinition = "TEXT")
    private String validatorCode; //validator code only written in cpp

    @Column(nullable = false)
    private Double timeLimit;

    @Column(nullable = false)
    private Integer spaceLimit;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    List<TestCase> testCases;

    @OneToMany(mappedBy = "problem")
    List<Submission> submissions;
}
