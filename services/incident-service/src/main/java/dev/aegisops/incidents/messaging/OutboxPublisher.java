package dev.aegisops.incidents.messaging;
import dev.aegisops.incidents.domain.OutboxEvent;
import dev.aegisops.incidents.repository.OutboxRepository;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class OutboxPublisher {
 private final OutboxRepository outbox;private final RabbitTemplate rabbit;
 public OutboxPublisher(OutboxRepository outbox,RabbitTemplate rabbit){this.outbox=outbox;this.rabbit=rabbit;}
 @Scheduled(fixedDelayString="${app.outbox.interval-ms:3000}") @Transactional
 public void publish(){
  for(OutboxEvent e:outbox.findTop20ByDeliveredAtIsNullOrderByCreatedAtAsc()){
   try{
    CorrelationData correlation=new CorrelationData(e.getEventId());
    Message msg=MessageBuilder.withBody(e.getPayloadJson().getBytes(StandardCharsets.UTF_8))
      .setContentType(MessageProperties.CONTENT_TYPE_JSON).setHeader("eventId",e.getEventId()).build();
    rabbit.send(RabbitConfiguration.EXCHANGE,e.getRoutingKey(),msg,correlation);
    CorrelationData.Confirm ack=correlation.getFuture().get(5,TimeUnit.SECONDS);
    if(ack.isAck() && correlation.getReturned()==null){e.markDelivered();outbox.save(e);}else break;
   }catch(Exception ex){break;} // retry next cycle; consumer deduplicates eventId
  }
 }
}
