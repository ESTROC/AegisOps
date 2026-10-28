package dev.aegisops.notifications;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Kafka is opt-in; Rabbit listener is disabled when Kafka is selected. */
@Component
@ConditionalOnProperty(name="app.events.transport",havingValue="kafka")
public class KafkaEventListener {
 private final ReceivedEventRepository repo;
 private final ObjectMapper mapper;
 public KafkaEventListener(ReceivedEventRepository repo,ObjectMapper mapper){this.repo=repo;this.mapper=mapper;}
 @KafkaListener(topics="aegisops.ticket.events",groupId="aegisops-notifications")
 @Transactional
 public void receive(ConsumerRecord<String,String> record)throws Exception{
   String id=record.key();
   if(id==null || id.isBlank())throw new IllegalArgumentException("Missing Kafka event ID");
   if(repo.existsByEventId(id))return;
   JsonNode payload=mapper.readTree(record.value());
   if(!payload.hasNonNull("ticketId"))throw new IllegalArgumentException("Missing ticketId");
   Header type=record.headers().lastHeader("eventType");
   String eventType=type==null?"ticket.unknown":new String(type.value(),StandardCharsets.UTF_8);
   repo.save(new ReceivedEvent(id,payload.get("ticketId").asLong(),eventType,
     payload.path("actor").asText(),payload.path("priority").asText(),payload.path("status").asText()));
 }
}
