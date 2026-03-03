package contest_site.contest_site.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(nullable = true)
    private Long contestId;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String problemName;

    @Column(nullable = true)
    private String difficulty;

    @Column(nullable = true)
    private Integer points=0;

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

    @OneToMany(mappedBy = "problem",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    List<Example> examples;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    List<TestCase> testCases;

    @OneToMany(mappedBy = "problem")
    @JsonIgnore
    List<Submission> submissions;
}
