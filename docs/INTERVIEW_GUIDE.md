# AegisOps — Technical interview guide

## 30-second pitch

“I built AegisOps as an independent full-stack Java portfolio project. It models real operational incident, problem, and change workflows with Java 21, Spring Boot, React, MySQL, JPA/Hibernate, Spring Security, and Docker. The design also demonstrates event-driven service boundaries through RabbitMQ and a transactional-outbox approach. I documented the architecture, wrote tests and prepared both GitHub Actions and Jenkins CI definitions.”

**Do not claim it is production deployed, has customers or achieves measured scale until you have evidence.** Your authentic Navy WESEE work remains separate.

## Likely deep-dive questions

1. **Why an outbox?** Database transaction commits business state + pending event atomically, avoiding an event published for a rolled-back ticket. Publisher retries after errors; consumer deduplicates by eventId.
2. **Exactly once?** No. Broker delivery is at least once; deduplication provides effectively once storage for each eventId under assumed DB constraints. No global exactly-once claim.
3. **How protect endpoints?** Signed JWT validation in Spring Security; role mapping; authenticated reads; engineer/admin writes; admin-only user provisioning.
4. **What is optimistic locking?** A `version` column rejects stale updates; clients must reload on 409 conflicts.
5. **How does Spring MVC map to JPA?** Controller validates DTO, service executes transaction, repository persists entity; Flyway schema and Hibernate validation reduce drift.
6. **Can tickets be closed from OPEN?** No. State machine enforces legal transitions; tests cover it.
7. **Why React?** SPA dashboard, ticket create/query/status updates, secure API client and visible audit history.
8. **CI/CD scope?** `mvn test`, frontend build, container build; deployment/promotion/rollback steps are future work and must not be claimed.
9. **Security limitations?** Demo JWT held in sessionStorage, no identity provider or refresh tokens. Production requires OIDC/BFF, TLS, rotation, rate limiting and secrets manager.
10. **What would you improve?** Testcontainers integration tests, resilience/circuit breakers, outbox leasing, OpenTelemetry tracing, dead-letter operator alerts, API gateway, deployment environments.

## Honest framing for WESEE

“I previously engineered authentication/session governance for a controlled Navy LAN. That gave me hands-on familiarity with secure API design, auditability and access control. AegisOps is a **separate independent Java project** where I applied similar *general* security principles in a Spring Boot stack. It contains no Navy software or operational data.”

## What you can add to the resume after building/running

- Spring Boot REST APIs and validation
- MySQL/Hibernate/JPA & Flyway
- React/Vite integration
- Spring Security JWT & RBAC
- RabbitMQ async events, outbox & idempotency
- Docker Compose, Nginx
- GitHub Actions and Jenkins pipeline
- JUnit 5 / Mockito tests
- Audit logs and SLA-based incident/problem tracking

Don't list production throughput, customer usage, SOAP, Hibernate optimization benchmarks or AWS deployment unless implemented and measured.
