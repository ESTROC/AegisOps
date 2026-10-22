package dev.aegisops.incidents.api;
import dev.aegisops.incidents.domain.*;
import jakarta.validation.constraints.*;
public class TicketRequests {
 private TicketRequests(){}
 public record Create(@NotNull TicketType type,@NotBlank @Size(max=180) String title,
  @NotBlank @Size(max=5000) String description,@NotBlank @Size(max=120) String serviceName,@NotNull Priority priority){}
 public record Transition(@NotNull TicketStatus status,@Size(max=100) String assignee,@NotNull Long version){}
 public record Comment(@NotBlank @Size(max=2000) String message){}
}
