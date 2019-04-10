package com.alanc.springboot.producer;

import com.alanc.springboot.entity.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: OrderSender
 * @Description: TODO 订单发生服务
 * @Author: alanc
 * @Date: 2019-04-02 16:25
 */
@Component
public class OrderSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Order order){

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(order.getMessageId());

        rabbitTemplate.convertAndSend("order-exchange",//exchange
                "order.abcd",
                order,//消息实体
                correlationData);//消息唯一id

    }
}
