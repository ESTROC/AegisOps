package dev.aegisops.incidents.domain;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
class TicketPolicyTest {
 @Test void slaPriorityOrdering(){assertEquals(Duration.ofHours(1),TicketPolicy.sla(Priority.P1));assertEquals(Duration.ofHours(72),TicketPolicy.sla(Priority.P4));}
 @Test void closedTicketIsTerminal(){assertFalse(TicketPolicy.canTransition(TicketStatus.CLOSED,TicketStatus.OPEN));}
 @Test void invalidTransitionThrows(){Ticket t=new Ticket(TicketType.INCIDENT,"t","d","api",Priority.P1,"admin",Instant.now());assertThrows(IllegalArgumentException.class,()->t.transition(TicketStatus.CLOSED,null,Instant.now()));}
 @Test void resolvedCanReopen(){assertTrue(TicketPolicy.canTransition(TicketStatus.RESOLVED,TicketStatus.INVESTIGATING));}
}
