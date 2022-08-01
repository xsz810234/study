package com.spring.common.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UDJDKInvocationHandle implements InvocationHandler {

    Object target ;

    public UDJDKInvocationHandle(Object obj){
        this.target = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("add".equals(method.getName())) {
            System.out.println("before proxy");
            Object invoke = method.invoke(target, args);
            System.out.println("after proxy");
            return invoke;
        }else {
            return method.invoke(target, args);
        }

    }
}
