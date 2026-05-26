package com.example.codereviewagent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubWebhookPayload(
        String action,
    String ref,
        Repository repository,
        @JsonProperty("pull_request") PullRequest pullRequest
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repository(String name, Owner owner) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(String login) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PullRequest(Integer number) {}
}
