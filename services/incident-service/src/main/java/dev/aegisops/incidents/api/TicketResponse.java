package dev.aegisops.incidents.api;
import dev.aegisops.incidents.domain.*;
import java.time.Instant;
public record TicketResponse(Long id,long version,TicketType type,String title,String description,String serviceName,
 Priority priority,TicketStatus status,String assignee,String createdBy,Instant createdAt,Instant updatedAt,Instant slaDeadline,boolean overdue){
 public static TicketResponse from(Ticket t){return new TicketResponse(t.getId(),t.getVersion(),t.getType(),t.getTitle(),t.getDescription(),t.getServiceName(),t.getPriority(),t.getStatus(),t.getAssignee(),t.getCreatedBy(),t.getCreatedAt(),t.getUpdatedAt(),t.getSlaDeadline(),t.getSlaDeadline().isBefore(Instant.now())&&t.getStatus()!=TicketStatus.RESOLVED&&t.getStatus()!=TicketStatus.CLOSED);}
}
