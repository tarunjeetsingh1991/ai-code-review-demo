# AI Code Review Agent - Spring Boot Demo

This project demonstrates an automated code review agent using:

- Java 21
- Spring Boot REST API
- GitHub REST API
- Jira Cloud REST API
- GitHub Actions trigger
- Build skipped in the workflow

## Run Locally

```bash
mvn spring-boot:run
```

Open Swagger:

```text
http://localhost:8080/swagger-ui.html
```

## Manual Review API

```bash
curl -X POST http://localhost:8080/api/reviews/run \
  -H "Content-Type: application/json" \
  -d '{
    "owner": "your-github-owner",
    "repo": "your-repo-name",
    "pullRequestNumber": 1
  }'
```

## Required Environment Variables

```bash
export GITHUB_TOKEN=your_github_token
export JIRA_BASE_URL=https://your-domain.atlassian.net
export JIRA_EMAIL=your-email@example.com
export JIRA_API_TOKEN=your_jira_api_token
export JIRA_PROJECT_KEY=SCRUM
export JIRA_ISSUE_TYPE=Task
```

## Dry Run Mode

By default:

```yaml
agent:
  dry-run: true
```

This means:
- GitHub comment is generated but not posted.
- Jira tickets are calculated but not created.

To enable real integration:

```yaml
agent:
  dry-run: false
```

## GitHub Actions Secret

Add this repository secret:

```text
AGENT_URL=https://your-deployed-agent-url
```

The GitHub Action skips build and only calls the Spring Boot review agent.
