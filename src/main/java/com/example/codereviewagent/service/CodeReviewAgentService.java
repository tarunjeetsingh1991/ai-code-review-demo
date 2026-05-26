package com.example.codereviewagent.service;

import com.example.codereviewagent.client.GitHubClient;
import com.example.codereviewagent.client.JiraClient;
import com.example.codereviewagent.config.AgentProperties;
import com.example.codereviewagent.dto.ChangedFile;
import com.example.codereviewagent.dto.Finding;
import com.example.codereviewagent.dto.ReviewRequest;
import com.example.codereviewagent.dto.ReviewResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class CodeReviewAgentService {

    private final GitHubClient gitHubClient;
    private final JiraClient jiraClient;
    private final SpecificationService specificationService;
    private final RuleBasedReviewEngine reviewEngine;
    private final ReviewCommentFormatter formatter;
    private final AgentProperties properties;

    public CodeReviewAgentService(
            GitHubClient gitHubClient,
            JiraClient jiraClient,
            SpecificationService specificationService,
            RuleBasedReviewEngine reviewEngine,
            ReviewCommentFormatter formatter,
            AgentProperties properties
    ) {
        this.gitHubClient = gitHubClient;
        this.jiraClient = jiraClient;
        this.specificationService = specificationService;
        this.reviewEngine = reviewEngine;
        this.formatter = formatter;
        this.properties = properties;
    }

    public ReviewResponse reviewPullRequest(ReviewRequest request) {
        List<ChangedFile> changedFiles = gitHubClient.getPullRequestFiles(
                request.owner(),
                request.repo(),
                request.pullRequestNumber()
        );

        String specification = specificationService.loadReviewSpecification();
        List<Finding> findings = reviewEngine.review(changedFiles, specification);
        String comment = formatter.format(findings);

        String githubStatus = "Skipped";
        String jiraStatus = "Skipped";

        if (!properties.isDryRun() && properties.isPostGithubComment()) {
            gitHubClient.postPullRequestComment(
                    request.owner(),
                    request.repo(),
                    request.pullRequestNumber(),
                    comment
            );
            githubStatus = "Posted PR comment";
        } else {
            githubStatus = "Dry run: comment generated but not posted";
        }

        List<Finding> jiraFindings = findings.stream()
                .filter(Finding::createJira)
                .toList();

        if (!properties.isDryRun() && properties.isCreateJiraTicket()) {
                System.out.println("Creating Jira tickets...");
                System.out.println("Jira findings count: " + jiraFindings.size());
            for (Finding finding : jiraFindings) {
                System.out.println("Creating Jira issue for: " + finding.title());
                jiraClient.createIssue(finding, request.owner(), request.repo(), request.pullRequestNumber());
            }
            jiraStatus = "Created " + jiraFindings.size() + " Jira ticket(s)";
        } else {
            jiraStatus = "Dry run: " + jiraFindings.size() + " Jira ticket(s) would be created";
        }

        boolean hasBlocking = findings.stream()
                .anyMatch(f -> "CRITICAL".equalsIgnoreCase(f.severity()) || "MAJOR".equalsIgnoreCase(f.severity()));

        return new ReviewResponse(
                request.owner(),
                request.repo(),
                request.pullRequestNumber(),
                hasBlocking ? "CHANGES_REQUESTED" : "PASSED",
                findings.size(),
                findings,
                githubStatus,
                jiraStatus
        );
    }

    public Optional<Integer> findOpenPullRequestNumber(String owner, String repo, String branch) {
        return gitHubClient.findOpenPullRequestNumber(owner, repo, branch);
    }
}
