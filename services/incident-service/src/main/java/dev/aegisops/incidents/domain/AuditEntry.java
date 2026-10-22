package dev.aegisops.incidents.domain;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="audit_entries")
public class AuditEntry {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="ticket_id",nullable=false) private Long ticketId;
 @Column(nullable=false) private String actor;
 @Column(nullable=false) private String action;
 @Column(nullable=false,length=500) private String detail;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 protected AuditEntry(){}
 public AuditEntry(Long ticketId,String actor,String action,String detail){this.ticketId=ticketId;this.actor=actor;this.action=action;this.detail=detail;this.createdAt=Instant.now();}
 public Long getId(){return id;} public Long getTicketId(){return ticketId;}public String getActor(){return actor;}public String getAction(){return action;}public String getDetail(){return detail;}public Instant getCreatedAt(){return createdAt;}
}
