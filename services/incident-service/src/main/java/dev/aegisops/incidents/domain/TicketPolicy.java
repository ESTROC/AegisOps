package dev.aegisops.incidents.domain;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
public final class TicketPolicy {
 private TicketPolicy() {}
 private static final Map<TicketStatus,Set<TicketStatus>> ALLOWED = Map.of(
  TicketStatus.OPEN,Set.of(TicketStatus.INVESTIGATING,TicketStatus.RESOLVED),
  TicketStatus.INVESTIGATING,Set.of(TicketStatus.RESOLVED),
  TicketStatus.RESOLVED,Set.of(TicketStatus.INVESTIGATING,TicketStatus.CLOSED),
  TicketStatus.CLOSED,Set.of());
 public static boolean canTransition(TicketStatus from, TicketStatus to) {return ALLOWED.get(from).contains(to);}
 public static Duration sla(Priority p) {return switch(p){case P1->Duration.ofHours(1);case P2->Duration.ofHours(4);case P3->Duration.ofHours(24);case P4->Duration.ofHours(72);};}
}
