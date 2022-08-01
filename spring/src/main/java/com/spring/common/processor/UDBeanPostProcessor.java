package com.spring.common.processor;

import org.springframework.beans.BeansException;

public interface UDBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName);

    public Object postProcessAfterInitialization(Object bean, String beanName);
}
