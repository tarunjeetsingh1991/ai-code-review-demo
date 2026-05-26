package com.example.codereviewagent.dto;

import java.util.List;

public record ReviewResponse(
        String owner,
        String repo,
        Integer pullRequestNumber,
        String decision,
        int totalFindings,
        List<Finding> findings,
        String githubCommentStatus,
        String jiraStatus
) {}
