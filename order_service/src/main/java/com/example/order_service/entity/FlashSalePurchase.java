package com.example.order_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name="flash_sale_purchase",
        indexes = {@Index(name="idx_campaign_user", columnList="campaign_id,user_id")}
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSalePurchase extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="campaign_id", nullable=false)
    private String campaignId;

    @Column(name="user_id", nullable=false)
    private String userId;


    @Column(name="order_id")
    private String orderId;


}