package com.example.demo.extra.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * RabbitListener绑定队列两种方式：
 *
 *     @RabbitListener(queues = {"${rabbitmq.queue.uniteQueue}"})
 *
 *     @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
 *                     value = @Queue(value = "${mq.config.queue}", durable = "true"),
 *                     exchange = @Exchange(value = "${mq.config.exchange}", type = ExchangeTypes.TOPIC),
 *                     key = "${mq.config.key}"), admin = "rabbitAdmin")
 */
@Slf4j
@Component
public class RabbitmqConsumer {

//    @RabbitListener(queues = {"${rabbitmq.queue.uniteQueue}"})
    public void getRabbitmqMessage(Message msg){
        log.info("--- RabbitmqConsumer --- getRabbitmqMessage -------- msg = " + msg.getBody());
    }

    /**
     * 手动ask：
     *
     */
//    @RabbitListener(queues = {"${rabbitmq.queue.uniteQueue}"})
    public void getRabbitmqMessage2(Message msg, Channel channel){
        log.info("--- RabbitmqConsumer --- getRabbitmqMessage -------- msg = " + msg.getBody());
        try {
            // 设置没有收到，会重发消息
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
