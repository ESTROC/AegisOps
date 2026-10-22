package dev.aegisops.notifications;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class TicketEventListener {
 private final ReceivedEventRepository repo;private final ObjectMapper mapper;
 public TicketEventListener(ReceivedEventRepository repo,ObjectMapper mapper){this.repo=repo;this.mapper=mapper;}
 @RabbitListener(queues="aegisops.notifications") @Transactional
 public void onTicketEvent(Message message)throws Exception{
  String id=(String)message.getMessageProperties().getHeaders().get("eventId");
  if(id==null)throw new IllegalArgumentException("Missing eventId header");
  if(repo.existsByEventId(id))return; // idempotent at-least-once delivery
  JsonNode payload=mapper.readTree(new String(message.getBody(),StandardCharsets.UTF_8));
  if(!payload.hasNonNull("ticketId"))throw new IllegalArgumentException("Missing ticketId");
  String eventType=message.getMessageProperties().getReceivedRoutingKey();
  repo.save(new ReceivedEvent(id,payload.get("ticketId").asLong(),eventType,payload.path("actor").asText(),payload.path("priority").asText(),payload.path("status").asText()));
 }
}
