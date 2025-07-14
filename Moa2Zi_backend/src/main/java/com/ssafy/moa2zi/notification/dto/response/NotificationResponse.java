package com.ssafy.moa2zi.notification.dto.response;


import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.notification.domain.Notification;
import com.ssafy.moa2zi.notification.domain.NotificationType;
import com.ssafy.moa2zi.notification.dto.SenderInfo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long notificationId,
        NotificationType type,
        SenderInfo sender,
        String content,
        Boolean isRead,
        LocalDateTime createdAt
) {

    public static NotificationResponse from(Member sender, Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getNotificationType(),
                SenderInfo.of(sender),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
