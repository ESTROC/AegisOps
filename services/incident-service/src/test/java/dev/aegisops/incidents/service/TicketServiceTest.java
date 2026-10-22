package dev.aegisops.incidents.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aegisops.incidents.api.*;
import dev.aegisops.incidents.domain.*;
import dev.aegisops.incidents.repository.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
class TicketServiceTest {
 @Test void missingTicketGets404(){
  TicketRepository tickets=mock(TicketRepository.class);
  when(tickets.findById(900L)).thenReturn(Optional.empty());
  TicketService svc=new TicketService(tickets,mock(AuditRepository.class),mock(CommentRepository.class),mock(OutboxRepository.class),new ObjectMapper());
  assertEquals(404,assertThrows(ResponseStatusException.class,()->svc.get(900L)).getStatusCode().value());
 }
}
