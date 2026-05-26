package com.example.codereviewagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "agent")
public class AgentProperties {
    private boolean dryRun = true;
    private boolean createJiraTicket = true;
    private boolean postGithubComment = true;

    public boolean isDryRun() { return dryRun; }
    public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }

    public boolean isCreateJiraTicket() { return createJiraTicket; }
    public void setCreateJiraTicket(boolean createJiraTicket) { this.createJiraTicket = createJiraTicket; }

    public boolean isPostGithubComment() { return postGithubComment; }
    public void setPostGithubComment(boolean postGithubComment) { this.postGithubComment = postGithubComment; }
}
