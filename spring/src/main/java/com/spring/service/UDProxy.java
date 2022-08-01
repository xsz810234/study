package com.spring.service;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyFactory;

public class UDProxy implements AopProxyFactory {
    @Override
    public AopProxy createAopProxy(AdvisedSupport advisedSupport) throws AopConfigException {
        return null;
    }
}
