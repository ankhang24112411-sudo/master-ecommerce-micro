package com.example.product_service;

import com.example.test.Chef;
import com.example.test.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class ProductServiceApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("CHECK SINGLETON SCOPE");
        Chef chef1 = applicationContext.getBean(Chef.class);
        Chef chef2 = applicationContext.getBean(Chef.class);
        System.out.println("CHEF1 == CHEF2" + (chef1 == chef2));

        System.out.println("CHECK PROTOTYPE SCOPE");
        Order order1 = applicationContext.getBean(Order.class);
        Order order2 = applicationContext.getBean(Order.class);
        System.out.println("ORDER1 == ORDER2" + (order1 == order2));

    }
}
