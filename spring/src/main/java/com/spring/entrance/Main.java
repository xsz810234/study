package com.spring.entrance;


import com.spring.common.UDApplicationContext;
import com.spring.common.config.AppConfig;
import com.spring.service.OrderService;
import com.spring.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        UDApplicationContext applicationContext = new UDApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        UserService userService2 = (UserService) applicationContext.getBean("userService");
        UserService userService3 = (UserService) applicationContext.getBean("userService");
        OrderService orderService = (OrderService) applicationContext.getBean("orderService");
        orderService.test("i am orderService");
        orderService.testUserService();
        System.out.println(applicationContext.beanPostProcessorList.size());
        userService.userServiceTest();
        System.out.println(userService.toString());
    }
}
