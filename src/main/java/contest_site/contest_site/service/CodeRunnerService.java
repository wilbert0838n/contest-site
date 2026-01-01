package contest_site.contest_site.service;
import contest_site.contest_site.model.Submission;

import java.io.IOException;

public interface CodeRunnerService {
    public void runCode(Submission submission) throws IOException, InterruptedException;
}
