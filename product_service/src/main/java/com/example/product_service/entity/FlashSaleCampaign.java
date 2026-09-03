package com.example.product_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "flash_sale_campaign")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleCampaign extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;


    @Column(name="product_id", nullable=false)
    private Long productId;



    @Column(name="start_at")
    private Instant startAt;



    @Column(name="end_at")
    private Instant endAt;


    private Integer stock;



    @Column(name="price_promo")
    private BigDecimal pricePromo;



    @Column(name="max_per_user")
    private Integer maxPerUser;



    @Column(name="created_at")
    private Instant createdAt;



    @Column(name="updated_at")
    private Instant updatedAt;



//    @PrePersist
//    public void prePersist(){
//
//        createdAt = LocalDateTime.now();
//
//        updatedAt = LocalDateTime.now();
//
//    }
//
//
//
//    @PreUpdate
//    public void preUpdate(){
//
//        updatedAt = LocalDateTime.now();
//
//    }


}
