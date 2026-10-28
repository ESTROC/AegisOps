-- Run with mysql after loading representative, synthetic tickets.
-- Compare EXPLAIN ANALYZE before vs after migration using the same data and hardware.
EXPLAIN ANALYZE SELECT id, status, created_at FROM tickets
 WHERE status='OPEN' ORDER BY created_at DESC LIMIT 50;
EXPLAIN ANALYZE SELECT id, type, status FROM tickets
 WHERE type='INCIDENT' AND status='OPEN' ORDER BY created_at DESC LIMIT 50;
EXPLAIN ANALYZE SELECT ticket_id, author, created_at FROM ticket_comments
 WHERE ticket_id=1 ORDER BY created_at DESC LIMIT 50;
