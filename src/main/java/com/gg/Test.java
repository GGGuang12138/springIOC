package com.gg;

import com.gg.framework.CustomApplicationContext;
import com.gg.service.OrderService;

/**
 * Creat by GG
 * Date on 2020/9/2  8:09 下午
 */
public class Test {
    public static void main(String[] args) {
        //对应AnnotationConfigApplicationContext
        //扫描 （判断是否存在Component） + 实例化（Bean的生命周期：1、实例化 2、依赖注入）
        //创建bean（spring 第一次启动加载非懒加载的bean）
        CustomApplicationContext customApplicationContext = new CustomApplicationContext(AppConfig.class);

        OrderService orderService = (OrderService) customApplicationContext.getBean("orderService");
        System.out.println(orderService);
        OrderService orderService1 = (OrderService) customApplicationContext.getBean("orderService");
        System.out.println(orderService);
        orderService.test();
        orderService1.test();



    }
}
