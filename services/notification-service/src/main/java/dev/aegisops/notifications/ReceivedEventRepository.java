package dev.aegisops.notifications;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReceivedEventRepository extends JpaRepository<ReceivedEvent,Long>{boolean existsByEventId(String eventId);}
