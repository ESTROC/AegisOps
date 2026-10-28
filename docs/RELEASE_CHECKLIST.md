# P2 release readiness checklist (synthetic portfolio)
- [ ] All Java/JUnit/Mockito services pass GitHub Actions CI.
- [ ] Browser Playwright and Java Selenium suites pass with evidence artifacts.
- [ ] Verify API viewer read-only restriction and unauthorized-request behavior.
- [ ] Validate RabbitMQ default, retry behavior, outbox deduplication and dead-letter route.
- [ ] If using Kafka: validate dedicated Kafka integration workflow and no Rabbit consumer in Kafka mode.
- [ ] Review ECS structured log output and X-Request-ID correlation.
- [ ] If using Splunk: verify HEC TLS, restricted index/token, redaction and retention.
- [ ] Run MySQL EXPLAIN ANALYZE on representative synthetic fixtures; record plan and p50/p95, without invented %.
- [ ] Inspect Docker health, error logs, actuator metrics and rollback plan.
- [ ] Review release issues in GitHub or authorized Jira/Rally board using Agile/Kanban workflow.
- [ ] Save exact commit SHA, test run URLs, approver, deployment target and limitations.

Not production-certified, not an Optum deployment, and not evidence of use of employer-controlled Jira/Rally integrations.
