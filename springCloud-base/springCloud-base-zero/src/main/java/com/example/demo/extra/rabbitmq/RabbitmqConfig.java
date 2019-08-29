package com.example.demo.extra.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitmqConfig {

    /**
     * 初始化Queue
     */
    @Bean("uniteQueue")
    public Queue uniteQueue(@Value("${rabbitmq.queue.uniteQueue}")
                            String uniteQueueName) {
        log.info("--- RabbitmqConfig --- uniteQueue --------- uniteQueueName = " + uniteQueueName);
        return new Queue(uniteQueueName);
    }

    /**
     * 初始化Exchage (Topic模式)
     */
    @Bean("uniteExchange")
    public TopicExchange uniteExchange(@Value("${rabbitmq.exchange.uniteExchange}")
                                       String uniteExchangeName){
        log.info("--- RabbitmqConfig --- uniteExchange --------- uniteExchangeName = " + uniteExchangeName);
        return new TopicExchange(uniteExchangeName);
    }

    /**
     * 绑定Queue到Exchage，并指定routingKey
     */
    @Bean
    public Binding bindUniteQueueExchange(@Qualifier("uniteQueue") Queue uniteQueue,
                                          @Qualifier("uniteExchange") TopicExchange uniteExchage,
                                          @Value("${rabbitmq.queue.uniteKey}") String routingKey){
        log.info("--- RabbitmqConfig --- bindUniteQueueExchange --------- uniteQueue = " + uniteQueue.getName() + " --- uniteExchange = " + uniteExchage.getName());
        return BindingBuilder.bind(uniteQueue).to(uniteExchage).with(routingKey);
    }

}
