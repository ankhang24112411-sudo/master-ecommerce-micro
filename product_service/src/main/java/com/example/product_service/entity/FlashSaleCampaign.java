package com.example.product_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "flash_sale_campaign")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleCampaign {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @Column(name="product_id", nullable=false)
    private String productId;


    @Column(name="start_at")
    private Instant startAt;


    @Column(name="end_at")
    private Instant endAt;


    private Integer stock;


    @Column(name="price_promo")
    private BigDecimal pricePromo;

    @Column(name="max_per_user")
    private Integer maxPerUser;


    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
//
//    @Column(name="created_at")
//    private Instant createdAt;
//
//    @Column(name="updated_at")
//    private Instant updatedAt;



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
