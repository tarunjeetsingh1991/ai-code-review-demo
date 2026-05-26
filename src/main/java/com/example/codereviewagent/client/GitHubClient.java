package com.example.codereviewagent.client;

import com.example.codereviewagent.config.GitHubProperties;
import com.example.codereviewagent.dto.ChangedFile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class GitHubClient {

    private final RestClient restClient;
    private final GitHubProperties properties;

    public GitHubClient(RestClient restClient, GitHubProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public List<ChangedFile> getPullRequestFiles(String owner, String repo, Integer prNumber) {
        String url = properties.getApiUrl() + "/repos/" + owner + "/" + repo + "/pulls/" + prNumber + "/files";

        GitHubFile[] files = restClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getToken())
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .retrieve()
                .body(GitHubFile[].class);

        if (files == null) {
            return List.of();
        }

        return Arrays.stream(files)
                .map(file -> new ChangedFile(file.filename(), file.status(), file.patch()))
                .toList();
    }

    public void postPullRequestComment(String owner, String repo, Integer prNumber, String body) {
        String url = properties.getApiUrl() + "/repos/" + owner + "/" + repo + "/issues/" + prNumber + "/comments";

        restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getToken())
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .body(Map.of("body", body))
                .retrieve()
                .toBodilessEntity();
    }

    public Optional<Integer> findOpenPullRequestNumber(String owner, String repo, String branch) {
        String url = properties.getApiUrl()
                + "/repos/" + owner + "/" + repo + "/pulls?state=open&head="
                + owner + ":" + branch + "&per_page=1";

        GitHubPull[] pulls = restClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getToken())
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .retrieve()
                .body(GitHubPull[].class);

        if (pulls == null || pulls.length == 0 || pulls[0] == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(pulls[0].number());
    }

    private record GitHubFile(String filename, String status, String patch) {}
    private record GitHubPull(Integer number) {}
}
