package com.example.product_service.repository;

import com.example.product_service.entity.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent,String> {
    @Query("select e from OutboxEvent e where e.status = 0 ORDER BY e.createdDate ASC")
    List<OutboxEvent> findPending(Pageable pageable);

    @Modifying
    @Query("UPDATE OutboxEvent e SET e.status = 1, e.publishedAt = :publishedAt WHERE e.id = :id")
    int markPublishedById(@Param("id") String id, @Param("publishedAt") Instant publishedAt);
}
