# Review Specification

## Goal
Review every pull request automatically using a Spring Boot agent.

## Build Rule
Build can be skipped for this demo. The agent focuses on changed-code review and workflow demonstration.

## Review Rules
The agent must check:
- No secrets, passwords, tokens, or API keys in code.
- Controller should not directly access repository.
- REST request bodies should use validation.
- Service methods that write to database should be transaction-safe.
- Use logging instead of System.out.println.
- Avoid swallowing generic exceptions.
- Check that implementation follows the specification.

## Jira Ticket Rule
Create Jira tickets only for:
- CRITICAL security issue.
- MAJOR architecture violation.
- Database transaction risk.
- Specification mismatch.

Do not create Jira tickets for minor formatting comments.
