package dev.aegisops.incidents.domain;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
@Entity @Table(name="tickets")
public class Ticket {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Version private long version;
 @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR) @Column(nullable=false) private TicketType type;
 @Column(nullable=false,length=180) private String title;
 @Column(nullable=false,columnDefinition="TEXT") private String description;
 @Column(name="service_name",nullable=false,length=120) private String serviceName;
 @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR) @Column(nullable=false) private Priority priority;
 @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.VARCHAR) @Column(nullable=false) private TicketStatus status;
 @Column(length=100) private String assignee;
 @Column(name="created_by",nullable=false,length=100) private String createdBy;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Column(name="sla_deadline",nullable=false) private Instant slaDeadline;
 protected Ticket(){}
 public Ticket(TicketType type,String title,String description,String serviceName,Priority priority,String actor,Instant now){
  this.type=type;this.title=title;this.description=description;this.serviceName=serviceName;this.priority=priority;
  this.status=TicketStatus.OPEN;this.createdBy=actor;this.createdAt=now;this.updatedAt=now;this.slaDeadline=now.plus(TicketPolicy.sla(priority));}
 public void transition(TicketStatus to,String assignee,Instant now){
  if(!TicketPolicy.canTransition(status,to))throw new IllegalArgumentException("Invalid status transition: "+status+" -> "+to);
  this.status=to;this.assignee=assignee;this.updatedAt=now;
 }
 public Long getId(){return id;} public long getVersion(){return version;}
 public TicketType getType(){return type;} public String getTitle(){return title;} public String getDescription(){return description;}
 public String getServiceName(){return serviceName;} public Priority getPriority(){return priority;}
 public TicketStatus getStatus(){return status;} public String getAssignee(){return assignee;}public String getCreatedBy(){return createdBy;}
 public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}public Instant getSlaDeadline(){return slaDeadline;}
}
