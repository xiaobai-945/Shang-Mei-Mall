package red.mlz.app.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public final static String SMS_EXCHANGE = "sms_exchange";
    public final static String SMS_QUEUE = "sms_queue";

    @Bean
    public Queue smsQueue() {
        return new Queue(SMS_QUEUE, true);
    }

    @Bean
    public TopicExchange smsExchange() {
        return new TopicExchange(SMS_EXCHANGE);
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange smsExchange) {
        return BindingBuilder.bind(smsQueue).to(smsExchange).with(SMS_QUEUE);
    }
}