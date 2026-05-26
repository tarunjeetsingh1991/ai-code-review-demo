package com.example.codereviewagent.service;

import com.example.codereviewagent.dto.Finding;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewCommentFormatter {

    public String format(List<Finding> findings) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Automated Code Review Agent\n\n");

        long critical = findings.stream().filter(f -> "CRITICAL".equalsIgnoreCase(f.severity())).count();
        long major = findings.stream().filter(f -> "MAJOR".equalsIgnoreCase(f.severity())).count();

        if (critical > 0 || major > 0) {
            sb.append("**Decision:** Changes requested.\n\n");
        } else {
            sb.append("**Decision:** Review passed with comments.\n\n");
        }

        sb.append("| Severity | File | Finding | Jira? |\n");
        sb.append("|---|---|---|---|\n");

        for (Finding finding : findings) {
            sb.append("| ")
                    .append(finding.severity()).append(" | ")
                    .append(finding.file()).append(" | ")
                    .append(finding.title()).append(" | ")
                    .append(finding.createJira() ? "Yes" : "No")
                    .append(" |\n");
        }

        sb.append("\n### Details\n");
        for (Finding finding : findings) {
            sb.append("\n#### ").append(finding.severity()).append(" - ").append(finding.title()).append("\n");
            sb.append("- File: `").append(finding.file()).append("`\n");
            sb.append("- ").append(finding.details()).append("\n");
        }

        return sb.toString();
    }
}
