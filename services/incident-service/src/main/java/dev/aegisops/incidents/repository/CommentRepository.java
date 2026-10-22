package dev.aegisops.incidents.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.aegisops.incidents.domain.TicketComment;
import java.util.List;
public interface CommentRepository extends JpaRepository<TicketComment,Long>{List<TicketComment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);}
