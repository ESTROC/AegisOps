# Quality engineering / Optum 2381067 evidence

This is an independent synthetic portfolio implementation, not a production client deployment.
The existing Java microservice CI, JUnit/Mockito tests and API smoke tests remain intact.

## P0: Browser and API regression

Start the application with a private .env file and `docker compose up --build -d`.
Set `APP_BASE_URL`, `TEST_ADMIN_USERNAME`, `TEST_ADMIN_PASSWORD` to a dedicated synthetic QA account.
Never use real user accounts or production data.

- `cd qa/playwright && npm install && npx playwright install chromium && npm test`
- `mvn -f qa/selenium/pom.xml test`
- `bash scripts/smoke.sh` after exporting bootstrap credentials.

Playwright covers authorized sign-in, ticket creation/search/audit, invalid credentials, viewer write denial
and payload validation. Selenium separately exercises the Java WebDriver sign-in and new-ticket dialog.
GitHub Actions saves HTML/JUnit reports, screenshots and traces on test failures.

## Measure before claiming improvements

Collect across at least 10 comparable CI runs:
- executed / passed / failed test counts by suite and overall pass rate;
- flaky attempts (pass after retry) / total attempts;
- median manual release-check time vs automated validation time;
- number of unique critical end-user workflows covered;
- query latency baseline and post-index result with sample size and controlled fixture.

No production uptime, time savings, percentage improvement, test count or bug reduction is asserted until measured.

## P2 release governance

Review the release checklist in `docs/RELEASE_CHECKLIST.md`.
Track defects and work items in the team's authorized Jira/Rally/Bitbucket or GitHub workspace.
Agile and Kanban are processes, not new libraries.
Do not claim integration with employer-managed tools from this public repository.
