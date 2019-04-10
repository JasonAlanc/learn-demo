package com.alanc.springboot;

import com.alanc.springboot.entity.Order;
import com.alanc.springboot.producer.OrderSender;
import com.alanc.springboot.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * @ClassName: ApplicationTest
 * @Description: TODO 测试producer
 * @Author: alanc
 * @Date: 2019-04-02 16:57
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private OrderSender orderSender;

    @Autowired
    private OrderService orderService;

    @Test
    public void testSend1() throws Exception{

        Order order = new Order();
        order.setId("20190402040211211");
        order.setMessageId("测试订单111");
        order.setName(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());

//        orderSender.send(order);
        orderService.createOrder(order);
    }
}
