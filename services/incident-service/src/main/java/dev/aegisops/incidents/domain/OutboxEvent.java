package dev.aegisops.incidents.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="outbox_events")
public class OutboxEvent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="event_id",nullable=false,unique=true) private String eventId;
 @Column(name="routing_key",nullable=false) private String routingKey;
 @Column(name="payload_json",nullable=false,columnDefinition="TEXT") private String payloadJson;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 @Column(name="delivered_at") private Instant deliveredAt;
 protected OutboxEvent(){}
 public OutboxEvent(String routingKey,String payloadJson){this.eventId=UUID.randomUUID().toString();this.routingKey=routingKey;this.payloadJson=payloadJson;this.createdAt=Instant.now();}
 public Long getId(){return id;} public String getEventId(){return eventId;} public String getRoutingKey(){return routingKey;}
 public String getPayloadJson(){return payloadJson;} public Instant getCreatedAt(){return createdAt;}
 public void markDelivered(){this.deliveredAt=Instant.now();}
}
