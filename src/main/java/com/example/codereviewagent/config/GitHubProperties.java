package com.example.codereviewagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "github")
public class GitHubProperties {
    private String token;
    private String apiUrl;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
}
