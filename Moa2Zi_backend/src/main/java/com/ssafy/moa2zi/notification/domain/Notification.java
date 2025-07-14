package com.ssafy.moa2zi.notification.domain;

import com.ssafy.moa2zi.common.entity.BaseTimeEntity;
import com.ssafy.moa2zi.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "notifications")
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    private Long senderId;

    private Long receiverId;

    private String message;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    private boolean isRead;

    @Builder
    public Notification (
            Long senderId,
            Long receiverId,
            String message,
            NotificationType notificationType
    ) {

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.isRead = false;
        this.notificationType = notificationType;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}
