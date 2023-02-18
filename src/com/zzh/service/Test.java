package com.zzh.service;

import com.zzh.spring.MyApplicationContext;
import com.zzh.spring.SpringConfig;

public class Test {

    public static void main(String[] args) {
        MyApplicationContext context = new MyApplicationContext(SpringConfig.class);
        UserService userService = (UserService) context.getBean("userService");
        UserService userService1 = (UserService) context.getBean("userService");
        UserService userService2 = (UserService) context.getBean("userService");
        System.out.println(userService);
        System.out.println(userService1);
        System.out.println(userService2);
        System.out.println(11111111);
    }
}
