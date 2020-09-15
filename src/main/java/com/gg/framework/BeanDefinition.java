package com.gg.framework;

/**
 * Creat by GG
 * Date on 2020/9/2  8:52 下午
 */
public class BeanDefinition { //bean的定义

    private String scope;
    private Boolean isLazy;
    private Class beanClass;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

}
