package dev.aegisops.incidents.api;
import dev.aegisops.incidents.domain.*;
import dev.aegisops.incidents.service.TicketService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/tickets")
public class TicketController {
 private final TicketService tickets;
 public TicketController(TicketService tickets){this.tickets=tickets;}
 @GetMapping public Page<TicketResponse> list(@RequestParam(required=false)TicketStatus status,@RequestParam(required=false)Priority priority,
  @RequestParam(required=false)TicketType type,@RequestParam(required=false)String q,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size){return tickets.list(status,priority,type,q,page,size);}
 @GetMapping("/{id}")public TicketResponse get(@PathVariable Long id){return tickets.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
 public TicketResponse create(@Valid @RequestBody TicketRequests.Create req,Principal actor){return tickets.create(req,actor.getName());}
 @PatchMapping("/{id}/status")@PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
 public TicketResponse status(@PathVariable Long id,@Valid @RequestBody TicketRequests.Transition req,Principal actor){return tickets.transition(id,req,actor.getName());}
 @PostMapping("/{id}/comments")@ResponseStatus(HttpStatus.CREATED)@PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
 public TicketComment comment(@PathVariable Long id,@Valid @RequestBody TicketRequests.Comment req,Principal actor){return tickets.comment(id,req,actor.getName());}
 @GetMapping("/{id}/comments")public List<TicketComment> comments(@PathVariable Long id){return tickets.comments(id);}
 @GetMapping("/{id}/audit")public List<AuditEntry> audit(@PathVariable Long id){return tickets.audit(id);}
}
