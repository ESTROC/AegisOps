# Optional Kafka and Splunk integration

## Transport selection

Default `docker compose up --build -d`: RabbitMQ, transactional outbox, consumer deduplication.
Optional `docker compose -f compose.yaml -f compose.kafka.yaml up --build -d`:
Kafka mode activates via `APP_EVENTS_TRANSPORT=kafka` on both Java services.
The producer attaches `eventId` as record key and `eventType` as a header.
The notification service has a conditional Kafka consumer; the Rabbit consumer is disabled in Kafka mode.
Because outbox delivery is at least once, event IDs remain unique in the notification database.
The two transport modes are exclusive to avoid duplicate processing.
Kafka's advertised address is internal to Docker; this override is for local integration testing, not a secure production broker.

`bash scripts/kafka-smoke.sh` validates a ticket event appears in the notification DB.
For real deployments add SASL/TLS, Kafka ACLs, dead-letter handling, retained-topic policies, lag alerts and partition/ordering evaluation.

## Splunk HEC

Spring Boot writes ECS structured JSON to stdout. `X-Request-ID` is validated or generated and included in MDC.
The `compose.splunk.yaml` override sends incident and notification logs to Splunk HEC using Docker's Splunk driver:
`docker compose -f compose.yaml -f compose.splunk.yaml up -d`.
Supply `SPLUNK_HEC_URL` (HTTPS) and `SPLUNK_HEC_TOKEN` via local environment/secret store.
Never commit the token or enable the optional driver when Splunk is unavailable.
Validate HEC TLS verification, network reachability, indexes, role-based dashboard access and logging retention.

Example SPL searches (adjust index and sources):
```spl
index=aegisops sourcetype=_json | stats count by service.name, log.level
index=aegisops sourcetype=_json | spath path=labels.requestId output=requestId | search requestId=* | table _time requestId message
```

No external Splunk instance is provisioned by this repository. Log output and the opt-in driver are implemented,
but actual HEC delivery requires a user's configured Splunk service.
