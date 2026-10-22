package dev.aegisops.incidents.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import dev.aegisops.incidents.domain.Ticket;
public interface TicketRepository extends JpaRepository<Ticket,Long>,JpaSpecificationExecutor<Ticket>{}
