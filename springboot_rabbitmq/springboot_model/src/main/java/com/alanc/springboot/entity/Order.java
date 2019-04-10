package com.alanc.springboot.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: Order
 * @Description: TODO 订单实体
 * @Author: alanc
 * @Date: 2019-04-02 16:15
 */
@Data
public class Order implements Serializable {

    private String id;

    private String name;

    private String messageId;//存储消息发生的唯一性id


}
