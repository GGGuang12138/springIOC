package com.gg.service;

import com.gg.framework.InitializingBean;
import com.gg.framework.annotation.Autowired;
import com.gg.framework.BeanNameAware;
import com.gg.framework.annotation.Component;
import com.gg.framework.annotation.Scope;

/**
 * Creat by GG
 * Date on 2020/9/2  8:15 下午
 */
@Component("orderService")
@Scope("prototype")
public class OrderService implements BeanNameAware, InitializingBean {
    @Autowired
    private UserService userService;

    private String beanName;

    private String username; //userService.getName()

    public void test() {
        System.out.println(userService);
    }

    public void setBeanName(String name) {
        this.beanName = beanName;
    }

    public void afterPropertiesSet() {
        System.out.println("初始化");
    }
}
