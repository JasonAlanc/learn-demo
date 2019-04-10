package com.alanc.springboot.utils;

import com.alanc.springboot.entity.Order;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
/**
 * 
 * @Author: alanc 
 * @Description: //TODO json工具类
 * @Date: 16:05 2019-04-07
 * @Param: 
 * @return: 
 **/
public class FastJsonConvertUtil {
    
    public static Order convertJSONToObject(String message, Object obj){
        Order order = JSON.parseObject(message, new TypeReference<Order>() {});
        return order;
    }

    public static String convertObjectToJSON(Object obj){
        String text = JSON.toJSONString(obj);
        return text;
    }
}
