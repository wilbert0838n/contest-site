package contest_site.contest_site.dto;

import contest_site.contest_site.service.Language;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDTO {
    public Long id;
    public String username;
    public Long problemId;
    public String language;
    public String code;
    private String verdict;
    private String executionTime;
    private String memoryUsed;
    private String submittedAt;
}
