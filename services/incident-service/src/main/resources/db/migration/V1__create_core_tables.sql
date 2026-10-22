CREATE TABLE app_users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE tickets (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT NOT NULL DEFAULT 0,
  type VARCHAR(20) NOT NULL,
  title VARCHAR(180) NOT NULL,
  description TEXT NOT NULL,
  service_name VARCHAR(120) NOT NULL,
  priority VARCHAR(20) NOT NULL,
  status VARCHAR(24) NOT NULL,
  assignee VARCHAR(100) NULL,
  created_by VARCHAR(100) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NOT NULL,
  sla_deadline TIMESTAMP(6) NOT NULL,
  INDEX idx_ticket_status_priority (status,priority),
  INDEX idx_ticket_created (created_at),
  INDEX idx_ticket_service (service_name)
);
CREATE TABLE ticket_comments (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ticket_id BIGINT NOT NULL,
  author VARCHAR(100) NOT NULL,
  message VARCHAR(2000) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  CONSTRAINT fk_comment_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);
CREATE TABLE audit_entries (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ticket_id BIGINT NOT NULL,
  actor VARCHAR(100) NOT NULL,
  action VARCHAR(80) NOT NULL,
  detail VARCHAR(500) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  INDEX idx_audit_ticket_created (ticket_id,created_at),
  CONSTRAINT fk_audit_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);
CREATE TABLE outbox_events (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  event_id VARCHAR(36) NOT NULL UNIQUE,
  routing_key VARCHAR(100) NOT NULL,
  payload_json TEXT NOT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  delivered_at TIMESTAMP(6) NULL,
  INDEX idx_outbox_delivery (delivered_at,created_at)
);
