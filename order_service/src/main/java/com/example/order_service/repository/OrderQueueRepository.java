package com.example.order_service.repository;

import com.example.order_service.entity.OrderQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderQueueRepository extends JpaRepository<OrderQueue,String > {
     Optional<OrderQueue> findByToken(String token);
}
