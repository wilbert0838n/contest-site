package contest_site.contest_site.dto;
import contest_site.contest_site.service.Language;
import lombok.Data;

@Data
public class CodeSubmission {
    private Long userId;
    private Long problemId;
    private Language language;
    private String code;
}
