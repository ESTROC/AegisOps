package dev.aegisops.notifications;
import java.time.Instant;
import jakarta.persistence.*;
@Entity @Table(name="received_events")
public class ReceivedEvent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="event_id",nullable=false,unique=true) private String eventId;
 @Column(name="ticket_id",nullable=false) private Long ticketId;
 @Column(name="event_type",nullable=false) private String eventType;
 @Column(nullable=false) private String actor;
 @Column(nullable=false) private String priority;
 @Column(nullable=false) private String status;
 @Column(name="received_at",nullable=false) private Instant receivedAt;
 protected ReceivedEvent(){}
 public ReceivedEvent(String eventId,Long ticketId,String eventType,String actor,String priority,String status){
  this.eventId=eventId;this.ticketId=ticketId;this.eventType=eventType;this.actor=actor;this.priority=priority;this.status=status;this.receivedAt=Instant.now();
 }
 public String getEventId(){return eventId;} public Long getTicketId(){return ticketId;}public String getEventType(){return eventType;}
}
