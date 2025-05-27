package red.mlz.console.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class RabbitMQConfig {
    // 定义队列名称
    public static final String GOODS_DELETE_QUEUE = "goods.delete.queue";

    // 创建队列
    @Bean
    public Queue goodsDeleteQueue() {
        return new Queue(GOODS_DELETE_QUEUE, true); // durable: 是否持久化
    }
}