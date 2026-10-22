package dev.aegisops.incidents.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.aegisops.incidents.domain.OutboxEvent;
import java.util.List;
public interface OutboxRepository extends JpaRepository<OutboxEvent,Long>{List<OutboxEvent> findTop20ByDeliveredAtIsNullOrderByCreatedAtAsc();}
