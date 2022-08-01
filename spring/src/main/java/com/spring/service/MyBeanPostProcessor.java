package com.spring.service;

import com.spring.common.annotation.component.UDComponent;
import com.spring.common.processor.UDBeanPostProcessor;

@UDComponent
public class MyBeanPostProcessor implements UDBeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("i am postProcessBeforeInitialization");
        System.out.println(bean.getClass());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("i am postProcessAfterInitialization");
        return bean;
    }
}
