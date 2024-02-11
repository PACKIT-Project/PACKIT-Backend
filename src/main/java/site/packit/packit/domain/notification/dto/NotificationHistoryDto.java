package site.packit.packit.domain.notification.dto;

import site.packit.packit.domain.notification.constant.NotificationType;

import java.time.LocalDateTime;

public record NotificationHistoryDto(
        String title,
        String content,
        NotificationType type,
        LocalDateTime time
) {
}
