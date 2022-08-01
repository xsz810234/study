package com.spring.service;

import com.spring.common.annotation.autowired.UDAutowired;
import com.spring.common.annotation.component.UDComponent;
import com.spring.common.annotation.scope.UDScope;

@UDComponent("orderService")
@UDScope("prototype")
public class OrderService {

    @UDAutowired
    UserService userService;

    public void testUserService(){
        userService.userServiceTest();
        System.out.println("i am xsz");
    }
    public void test(String message){
        System.out.println(message);
    }
}
