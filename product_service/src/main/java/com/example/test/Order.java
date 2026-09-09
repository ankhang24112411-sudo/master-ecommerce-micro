package com.example.test;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Order {
    public Order(){
        System.out.println("create new order");
    }
}
