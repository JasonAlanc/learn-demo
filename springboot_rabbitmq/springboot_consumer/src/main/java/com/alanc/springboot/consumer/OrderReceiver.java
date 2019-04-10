package com.alanc.springboot.consumer;

import com.alanc.springboot.entity.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: OrderReceiver
 * @Description: TODO 消费端
 * @Author: alanc
 * @Date: 2019-04-02 19:19
 */
@Component
public class OrderReceiver {

    /*@RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order-queue", durable = "true"),
            exchange = @Exchange(name = "order-exchange", durable = "true", type = "topic"),
            key = "order.*"
    ))*/
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}"),
            ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationException}",
            key = "${spring.rabbitmq.listener.order.key}"
    ))
    @RabbitHandler
    public void onOrderMessage(@Payload Order order, @Headers Map<String, Object> headers, Channel channel)throws Exception{
        //消费者操作
        System.err.println("-----------收到消息，开始消费-----------");
        System.err.println("订单ID：" + order.getId());

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        //ack
        channel.basicAck(deliveryTag, false);
    }

}
