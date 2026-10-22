package dev.aegisops.notifications;
import java.util.Map;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class MessagingConfiguration {
 @Bean TopicExchange exchange(){return new TopicExchange("aegisops.events",true,false);}
 @Bean DirectExchange deadLetterExchange(){return new DirectExchange("aegisops.deadletter",true,false);}
 @Bean Queue deadLetterQueue(){return QueueBuilder.durable("aegisops.notifications.dlq").build();}
 @Bean Binding deadLetterBinding(Queue deadLetterQueue,DirectExchange deadLetterExchange){return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("deadletter");}
 @Bean Queue notificationQueue(){return QueueBuilder.durable("aegisops.notifications").withArguments(Map.of("x-dead-letter-exchange","aegisops.deadletter","x-dead-letter-routing-key","deadletter")).build();}
 @Bean Binding notificationBinding(Queue notificationQueue,TopicExchange exchange){return BindingBuilder.bind(notificationQueue).to(exchange).with("ticket.*");}
}
