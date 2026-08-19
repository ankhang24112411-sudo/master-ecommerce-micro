package com.example.order_service.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderItemEntity extends BaseEntity{

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "price")
    private Integer price;

    @Column(name = "quantity")
    private Integer quantity;
}
