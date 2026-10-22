package dev.aegisops.incidents.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.aegisops.incidents.domain.AuditEntry;
import java.util.List;
public interface AuditRepository extends JpaRepository<AuditEntry,Long>{List<AuditEntry> findByTicketIdOrderByCreatedAtDesc(Long ticketId);}
