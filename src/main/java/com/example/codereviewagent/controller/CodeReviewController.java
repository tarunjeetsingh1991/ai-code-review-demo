package com.example.codereviewagent.controller;

import com.example.codereviewagent.dto.GitHubWebhookPayload;
import com.example.codereviewagent.dto.ReviewRequest;
import com.example.codereviewagent.dto.ReviewResponse;
import com.example.codereviewagent.service.CodeReviewAgentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class CodeReviewController {

    private final CodeReviewAgentService service;

    public CodeReviewController(CodeReviewAgentService service) {
        this.service = service;
    }
    
    @GetMapping("/home")
    public String getHomePage()
    {
        System.out.println("password=123");
    	return "<h1> Home Page </h1>";   
    }
    @PostMapping("/run")
    public ResponseEntity<ReviewResponse> runManualReview(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(service.reviewPullRequest(request));
    }

    @PostMapping("/github-webhook")
    public ResponseEntity<?> handleGitHubWebhook(@RequestBody GitHubWebhookPayload payload) {
        if (payload.repository() == null || payload.repository().owner() == null) {
            return ResponseEntity.badRequest().body("Invalid payload: repository owner is missing");
        }

        Integer prNumber = payload.pullRequest() != null ? payload.pullRequest().number() : null;

        if (prNumber == null && payload.ref() != null && payload.ref().startsWith("refs/heads/")) {
            String branch = payload.ref().substring("refs/heads/".length());
            prNumber = service.findOpenPullRequestNumber(
                    payload.repository().owner().login(),
                    payload.repository().name(),
                    branch
            ).orElse(null);
        }

        if (prNumber == null) {
            return ResponseEntity.ok("Ignored event: no pull request found for payload");
        }

        ReviewRequest request = new ReviewRequest(
                payload.repository().owner().login(),
                payload.repository().name(),
                prNumber
        );

        return ResponseEntity.ok(service.reviewPullRequest(request));
    }
}
