package com.spring.service;

import com.spring.service.test.TestDao;

public class TestDaoImpl implements TestDao {
    private Integer c;
    @Override
    public Integer add(Integer a, Integer b){
        System.out.println("i am test TestDaoImpl add ");
        c = a +b;
        return a+b;
    }

    @Override
    public Integer delete(Integer a, Integer b) {
        System.out.println("i am test TestDaoImpl delete");
        return a-b;
    }
}
