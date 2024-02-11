package site.packit.packit.domain.notification.dto;

import site.packit.packit.domain.notification.constant.NotificationType;
import site.packit.packit.domain.notification.entity.PushNotificationSubscriber;

import java.util.List;

import static site.packit.packit.domain.notification.constant.NotificationType.ACTIVE;
import static site.packit.packit.domain.notification.constant.NotificationType.TRAVEL_REMIND;

public record NotificationConfigStatus(
        boolean enableActiveNotification,
        boolean enableTravelRemindNotification
) {
    public NotificationConfigStatus(boolean enableActiveNotification, boolean enableTravelRemindNotification) {
        this.enableActiveNotification = enableActiveNotification;
        this.enableTravelRemindNotification = enableTravelRemindNotification;
    }

    public static NotificationConfigStatus notNotificationConfig() {
        return new NotificationConfigStatus(false, false);
    }

    public static NotificationConfigStatus of(List<PushNotificationSubscriber> notificationSubscribers) {
        List<NotificationType> notificationTypes = notificationSubscribers.stream()
                .map(PushNotificationSubscriber::getNotificationType)
                .toList();

        return new NotificationConfigStatus(
                notificationTypes.contains(ACTIVE),
                notificationTypes.contains(TRAVEL_REMIND)
        );
    }
}
