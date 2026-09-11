package org.example.notificationsservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.notificationsservice.config.enums.CampaignSegment;
import org.example.notificationsservice.config.enums.CampaignStatus;
import org.example.notificationsservice.config.enums.NotificationChannel;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "notification_campaign",
       indexes = {@Index(
               name = "idx_campaign_status_scheduled_at",
               columnList = "status, scheduled_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * ALL_USERS
     * VIP_USERS
     * NEW_USERS
     * INACTIVE_USERS
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "segment", nullable = false, length = 100)
    private CampaignSegment segment;

    @Enumerated(EnumType.STRING)
    @Column(name = "channels", nullable = false)
    private Set<NotificationChannel> channels = new HashSet<>();

    /**
     * Có thể là:
     *
     * template name
     * template id
     *
     * Ví dụ:
     * FLASH_SALE_NOTIFICATION
     */
    @Column(name = "template", nullable = false, length = 255)
    private String template;

    /**
     * Thời điểm campaign bắt đầu chạy.
     */
    @Column(name = "scheduled_at",nullable = false)
    private Instant scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CampaignStatus campaignStatus;
}
