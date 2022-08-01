package com.spring.service;

import com.spring.common.annotation.autowired.UDAutowired;
import com.spring.common.annotation.component.UDComponent;
import com.spring.common.annotation.scope.UDScope;
import com.spring.common.bean.UDInitializingBean;
import com.spring.common.model.User;

@UDComponent("userService")
//@UDScope("prototype")
public class UserService implements UDInitializingBean {

    private User defaultUser;

    @UDAutowired(required = true)
    OrderService orderService;

    public void userServiceTest(){

        orderService.test("i am userServiceTest");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        defaultUser = new User();
        System.out.println("i am afterPropertiesSet");
    }
}
