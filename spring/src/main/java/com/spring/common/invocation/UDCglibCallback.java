package com.spring.common.invocation;


import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class UDCglibCallback implements MethodInterceptor {

    /**
     *
     * @param o 代理对象
     * @param method 目标对象中的方法
     * @param args 目标对象方法的参数
     * @param methodProxy 代理对象中的方法
     * @return
     * @throws Throwable
     */

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("befroe cglib proxy");
        Object target = methodProxy.invokeSuper(o, args);
        System.out.println("after cglib proxy");
        return target;
    }
}
