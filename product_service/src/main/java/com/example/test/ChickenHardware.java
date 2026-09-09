package com.example.test;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ChickenHardware {
    public ChickenHardware(){
        System.out.println("Khi object");
    }
    @PostConstruct
    public void init(){
        System.out.println("Bean bat day san sang dc su dung");
    }
    @PreDestroy
    public void cleanup(){
        System.out.println("Bean da bi huy");
    }
}
