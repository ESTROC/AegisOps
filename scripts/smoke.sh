#!/usr/bin/env bash
set -Eeuo pipefail
BASE="${BASE:-http://localhost:8088}"
: "${BOOTSTRAP_ADMIN_USERNAME:?Set BOOTSTRAP_ADMIN_USERNAME from your .env}"
: "${BOOTSTRAP_ADMIN_PASSWORD:?Set BOOTSTRAP_ADMIN_PASSWORD from your .env}"
command -v python3 >/dev/null || { echo 'python3 required'; exit 1; }
echo 'Check health'
curl -fsS "$BASE/actuator/health" | grep -q 'UP'
echo 'Authenticate'
TOKEN=$(curl -fsS "$BASE/api/auth/login" -H 'Content-Type: application/json'  -d "$(BOOTSTRAP_ADMIN_USERNAME="$BOOTSTRAP_ADMIN_USERNAME" BOOTSTRAP_ADMIN_PASSWORD="$BOOTSTRAP_ADMIN_PASSWORD" python3 -c 'import os,json;print(json.dumps({"username":os.environ["BOOTSTRAP_ADMIN_USERNAME"],"password":os.environ["BOOTSTRAP_ADMIN_PASSWORD"]}))')"  | python3 -c 'import json,sys; print(json.load(sys.stdin)["accessToken"])')
echo 'Create incident'
RESP=$(curl -fsS -X POST "$BASE/api/tickets" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json'  -d '{"type":"INCIDENT","title":"Smoke test API latency","description":"Synthetic incident for test only","serviceName":"example-api","priority":"P2"}')
ID=$(echo "$RESP" | python3 -c 'import sys,json;print(json.load(sys.stdin)["id"])')
VERSION=$(echo "$RESP" | python3 -c 'import sys,json;print(json.load(sys.stdin)["version"])')
echo "Created ticket id=$ID; transition"
curl -fsS -X PATCH "$BASE/api/tickets/$ID/status" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json'  -d "{\"status\":\"INVESTIGATING\",\"assignee\":\"smoke-engineer\",\"version\":$VERSION}" >/dev/null
curl -fsS "$BASE/api/tickets/$ID/audit" -H "Authorization: Bearer $TOKEN" | grep -q 'STATUS_CHANGE'
echo 'Reject direct terminal transition as 409'
INVALID_STATUS=$(curl -s -o /dev/null -w '%{http_code}' -X PATCH "$BASE/api/tickets/$ID/status" -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
 -d "{\"status\":\"CLOSED\",\"assignee\":\"smoke-engineer\",\"version\":$VERSION}")
if [[ "$INVALID_STATUS" != "409" ]]; then echo "Expected 409, got $INVALID_STATUS"; exit 1; fi
SON=$(curl -fsS "$BASE/api/tickets/$ID/legacy-health" -H "Authorization: Bearer $TOKEN")
echo "$SON" | grep -q 'SYNTHETIC_LEGACY_SOAP_ADAPTER'
echo 'SMOKE PASS: auth, ticket workflow, invalid transition, SOAP adapter and audit.' 
