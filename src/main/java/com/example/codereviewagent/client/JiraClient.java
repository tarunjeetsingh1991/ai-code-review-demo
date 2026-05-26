package com.example.codereviewagent.client;

import com.example.codereviewagent.config.JiraProperties;
import com.example.codereviewagent.dto.Finding;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class JiraClient {

    private final RestClient restClient;
    private final JiraProperties properties;

    public JiraClient(RestClient restClient, JiraProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public void createIssue(Finding finding, String owner, String repo, Integer prNumber) {
        String url = properties.getBaseUrl() + "/rest/api/3/issue";
        String auth = Base64.getEncoder().encodeToString(
                (properties.getEmail() + ":" + properties.getApiToken()).getBytes(StandardCharsets.UTF_8)
        );

        Map<String, Object> body = Map.of(
                "fields", Map.of(
                        "project", Map.of("key", properties.getProjectKey()),
                        "summary", "[Code Review] " + finding.title(),
                        "issuetype", Map.of("name", properties.getIssueType()),
                        "description", adfDescription(finding, owner, repo, prNumber)
                )
        );

        restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private Map<String, Object> adfDescription(Finding finding, String owner, String repo, Integer prNumber) {
        String text = """
                Repository: %s/%s
                Pull Request: #%d
                Severity: %s
                File: %s

                Details:
                %s
                """.formatted(owner, repo, prNumber, finding.severity(), finding.file(), finding.details());

        return Map.of(
                "type", "doc",
                "version", 1,
                "content", List.of(Map.of(
                        "type", "paragraph",
                        "content", List.of(Map.of("type", "text", "text", text))
                ))
        );
    }
}
