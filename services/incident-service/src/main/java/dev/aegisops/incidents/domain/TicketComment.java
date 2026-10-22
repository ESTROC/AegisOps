package dev.aegisops.incidents.domain;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="ticket_comments")
public class TicketComment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="ticket_id",nullable=false) private Long ticketId;
 @Column(nullable=false) private String author;
 @Column(nullable=false,length=2000) private String message;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 protected TicketComment(){}
 public TicketComment(Long ticketId,String author,String message){this.ticketId=ticketId;this.author=author;this.message=message;this.createdAt=Instant.now();}
 public Long getId(){return id;}public Long getTicketId(){return ticketId;}public String getAuthor(){return author;}public String getMessage(){return message;}public Instant getCreatedAt(){return createdAt;}
}
