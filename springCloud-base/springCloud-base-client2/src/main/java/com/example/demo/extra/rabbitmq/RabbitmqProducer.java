package com.example.demo.extra.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitmqProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendRabbitmqMessage(String exchage, String routingKey, String msg) {
        log.info("--- RabbitmqProducer --- sendRabbitmqMessage ------- exchage = " + exchage + " --- routingKey = " + routingKey + " --- msg = " + msg);
        rabbitTemplate.convertAndSend(exchage, routingKey, msg);
    }

}
