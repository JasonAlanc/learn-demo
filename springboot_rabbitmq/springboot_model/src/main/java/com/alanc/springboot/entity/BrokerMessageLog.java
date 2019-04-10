package com.alanc.springboot.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: BrokerMessageLog
 * @Description: TODO
 * @Author: alanc
 * @Date: 2019-04-02 16:18
 */
@Data
public class BrokerMessageLog {

    private String messageId;

    private String message;

    private Integer tryCount;

    private String status;

    private Date nextRetry;

    private Date createTime;

    private Date updateTime;

}
