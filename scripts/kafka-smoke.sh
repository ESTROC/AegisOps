#!/usr/bin/env bash
set -Eeuo pipefail
BASE="${BASE:-http://127.0.0.1:8088}"
: "${BOOTSTRAP_ADMIN_USERNAME:?Set a synthetic admin username}"
: "${BOOTSTRAP_ADMIN_PASSWORD:?Set a synthetic admin password}"
: "${MYSQL_PASSWORD:?Set test database password}"
COMPOSE=(docker compose -f compose.yaml -f compose.kafka.yaml)
AUTH=$(BOOTSTRAP_ADMIN_USERNAME="$BOOTSTRAP_ADMIN_USERNAME" BOOTSTRAP_ADMIN_PASSWORD="$BOOTSTRAP_ADMIN_PASSWORD" python3 -c 'import os,json;print(json.dumps({"username":os.environ["BOOTSTRAP_ADMIN_USERNAME"],"password":os.environ["BOOTSTRAP_ADMIN_PASSWORD"]}))')
TOKEN=$(curl -fsS "$BASE/api/auth/login" -H 'Content-Type: application/json' -d "$AUTH" | python3 -c 'import sys,json;print(json.load(sys.stdin)["accessToken"])')
TITLE="Kafka integration smoke $(date +%s)"
PAYLOAD=$(TEST_TITLE="$TITLE" python3 -c 'import os,json;print(json.dumps({"type":"INCIDENT","title":os.environ["TEST_TITLE"],"description":"Synthetic Kafka delivery test","serviceName":"kafka-smoke","priority":"P2"}))')
ID=$(curl -fsS -X POST "$BASE/api/tickets" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d "$PAYLOAD" | python3 -c 'import sys,json;print(json.load(sys.stdin)["id"])')
echo "Waiting for Kafka event delivery for synthetic ticket $ID..."
for attempt in $(seq 1 40); do
 count=$("${COMPOSE[@]}" exec -T -e MYSQL_PWD="$MYSQL_PASSWORD" mysql mysql -u aegisops -N -B -D aegisops_notifications -e "SELECT COUNT(*) FROM received_events WHERE ticket_id=$ID;" 2>/dev/null || true)
 if [[ "$count" =~ ^[0-9]+$ ]] && (( count >= 1 )); then echo "Kafka delivery PASS: ticket $ID"; exit 0; fi
 sleep 3
done
echo "Kafka delivery failed or not observed" >&2
exit 1
