-- Add indexes to support common paginated UI and ticket history queries.
-- Validate with EXPLAIN ANALYZE on representative synthetic datasets before claiming speedups.
CREATE INDEX idx_tickets_status_created_id ON tickets(status, created_at, id);
CREATE INDEX idx_tickets_type_status_created ON tickets(type, status, created_at);
CREATE INDEX idx_comments_ticket_created ON ticket_comments(ticket_id, created_at);
CREATE INDEX idx_tickets_priority_sla ON tickets(priority, sla_deadline);
