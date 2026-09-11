package org.example.notificationsservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.notificationsservice.config.enums.NotificationChannel;
import org.example.notificationsservice.config.enums.NotificationStatus;

import java.time.Instant;

@Entity
@Table(name = "notification_log",
        indexes = @Index(name = "idx_notif_progress",
                columnList = "campaign_id,status,channel"))
@Getter
@Setter
@NoArgsConstructor
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "campaign_id", nullable = false)
    private String campaignId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationStatus status;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(columnDefinition = "text")
    private String error;
}