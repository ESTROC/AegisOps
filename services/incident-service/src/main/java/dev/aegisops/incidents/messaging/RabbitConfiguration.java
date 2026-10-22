package dev.aegisops.incidents.messaging;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitConfiguration {
 public static final String EXCHANGE="aegisops.events";
 @Bean TopicExchange ticketExchange(){return new TopicExchange(EXCHANGE,true,false);}
}
