package com.example.support;

public class BeanAttr {

    private String beanName;

    private String filedName;

    private boolean isFactoryBean;


    public BeanAttr(String beanName, String filedName, boolean isFactoryBean) {
        this.beanName = beanName;
        this.filedName = filedName;
        this.isFactoryBean = isFactoryBean;
    }

    public boolean isFactoryBean() {
        return isFactoryBean;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getFiledName() {
        return filedName;
    }
}

