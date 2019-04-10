package com.alanc.springboot.utils;

import java.util.Date;

/**
 *
 * @Author: alanc
 * @Description: //TODO 日期工具类
 * @Date: 16:05 2019-04-07
 * @Param:
 * @return:
 **/
public class DateUtils {
    public static Date addMinutes(Date orderTime, int orderTimeout) {
        Date afterDate = new Date(orderTime.getTime() + 60000*orderTimeout);
        return afterDate;
    }
}
