package dev.aegisops.incidents.domain;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
class TicketTest {
 @Test void recordsSlaForPriority(){
  Instant now=Instant.parse("2026-10-20T10:00:00Z");
  Ticket t=new Ticket(TicketType.PROBLEM,"slow db","db lag","database",Priority.P1,"engineer",now);
  assertEquals(now.plusSeconds(3600),t.getSlaDeadline());
  assertEquals(TicketStatus.OPEN,t.getStatus());
 }
 @Test void legalTransitionRecordsUpdate(){
  Instant now=Instant.parse("2026-10-20T10:00:00Z");
  Ticket t=new Ticket(TicketType.CHANGE,"rollout","planned","web",Priority.P3,"engineer",now);
  t.transition(TicketStatus.INVESTIGATING,"engineer2",now.plusSeconds(20));
  assertEquals(TicketStatus.INVESTIGATING,t.getStatus());
  assertEquals("engineer2",t.getAssignee());
 }
}
