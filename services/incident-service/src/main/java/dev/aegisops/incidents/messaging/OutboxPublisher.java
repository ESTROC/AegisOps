package dev.aegisops.incidents.messaging;
import dev.aegisops.incidents.domain.OutboxEvent;
import dev.aegisops.incidents.repository.OutboxRepository;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** One outbox, configurable transport. RabbitMQ is the safe default.
 * Both transports acknowledge before the outbox is marked delivered.
 * A crash between ack and commit may redeliver; consumers deduplicate by eventId.
 */
@Component
public class OutboxPublisher {
 private static final String KAFKA_TOPIC = "aegisops.ticket.events";
 private final OutboxRepository outbox;
 private final RabbitTemplate rabbit;
 private final KafkaTemplate<String,String> kafka;
 private final String transport;
 public OutboxPublisher(OutboxRepository outbox,RabbitTemplate rabbit,KafkaTemplate<String,String> kafka,
    @Value("${app.events.transport:rabbit}")String transport){
   this.outbox=outbox;this.rabbit=rabbit;this.kafka=kafka;
   if(!transport.equals("rabbit")&&!transport.equals("kafka"))throw new IllegalArgumentException("Unsupported event transport: "+transport);
   this.transport=transport;
 }
 @Scheduled(fixedDelayString="${app.outbox.interval-ms:3000}") @Transactional
 public void publish(){
  for(OutboxEvent e:outbox.findTop20ByDeliveredAtIsNullOrderByCreatedAtAsc()){
   try{
    if("kafka".equals(transport)){
      ProducerRecord<String,String> record=new ProducerRecord<>(KAFKA_TOPIC,e.getEventId(),e.getPayloadJson());
      record.headers().add("eventType",e.getRoutingKey().getBytes(StandardCharsets.UTF_8));
      kafka.send(record).get(5,TimeUnit.SECONDS);
    }else{
      CorrelationData correlation=new CorrelationData(e.getEventId());
      Message msg=MessageBuilder.withBody(e.getPayloadJson().getBytes(StandardCharsets.UTF_8))
        .setContentType(MessageProperties.CONTENT_TYPE_JSON).setHeader("eventId",e.getEventId()).build();
      rabbit.send(RabbitConfiguration.EXCHANGE,e.getRoutingKey(),msg,correlation);
      CorrelationData.Confirm ack=correlation.getFuture().get(5,TimeUnit.SECONDS);
      if(!ack.isAck()||correlation.getReturned()!=null)break;
    }
    e.markDelivered();
    outbox.save(e);
   }catch(Exception ex){break;} // Leave undelivered; retry next cycle. Do not leak payloads in logs.
  }
 }
}
