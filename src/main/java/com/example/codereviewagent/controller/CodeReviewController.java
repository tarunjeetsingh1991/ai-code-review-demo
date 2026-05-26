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

    @PostMapping("/run")
    public ResponseEntity<ReviewResponse> runManualReview(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(service.reviewPullRequest(request));
    }

    @PostMapping("/github-webhook")
    public ResponseEntity<?> handleGitHubWebhook(@RequestBody GitHubWebhookPayload payload) {
        if (payload.pullRequest() == null || payload.repository() == null) {
            return ResponseEntity.ok("Ignored non pull-request event");
        }

        ReviewRequest request = new ReviewRequest(
                payload.repository().owner().login(),
                payload.repository().name(),
                payload.pullRequest().number()
        );

        return ResponseEntity.ok(service.reviewPullRequest(request));
    }
}
