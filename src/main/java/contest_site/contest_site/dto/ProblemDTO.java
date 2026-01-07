package contest_site.contest_site.dto;
import lombok.Data;
import java.util.List;

@Data
public class ProblemDTO {
    private Long id;
    private String problemName;
    private String difficulty;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private Double timeLimit;
    private Integer spaceLimit;
    private List<ExampleDTO> examples;

    private Integer solved = 0;
    private Integer total = 0;
    private Double accuracy = 0.0;
}
