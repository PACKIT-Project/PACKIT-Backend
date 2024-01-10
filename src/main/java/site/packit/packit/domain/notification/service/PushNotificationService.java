package site.packit.packit.domain.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.notification.entity.PushNotificationSubscriber;
import site.packit.packit.domain.notification.exception.NotificationException;
import site.packit.packit.domain.notification.repository.PushNotificationSubscriberRepository;

import static site.packit.packit.domain.notification.exception.NotificationErrorCode.PUSH_NOTIFICATION_SUBSCRIBER_ALREADY_EXIST;
import static site.packit.packit.domain.notification.exception.NotificationErrorCode.PUSH_NOTIFICATION_SUBSCRIBER_NOT_FOUND;

@Transactional
@Service
public class PushNotificationService {

    private PushNotificationSubscriberRepository pushNotificationSubscriberRepository;

    public PushNotificationService(PushNotificationSubscriberRepository pushNotificationSubscriberRepository) {
        this.pushNotificationSubscriberRepository = pushNotificationSubscriberRepository;
    }

    public void createSubscriber(Member member) {
        checkSubscriberExist(member);
        pushNotificationSubscriberRepository.save(new PushNotificationSubscriber(member));
    }

    private void checkSubscriberExist(Member member) {
        boolean isExist = isPushNotificationSubscriber(member);

        if (isExist) {
            throw new NotificationException(PUSH_NOTIFICATION_SUBSCRIBER_ALREADY_EXIST);
        }
    }

    public void deleteSubscriber(Member member) {
        PushNotificationSubscriber pushNotificationSubscriber = pushNotificationSubscriberRepository.findByMember(member)
                .orElseThrow(() -> new NotificationException(PUSH_NOTIFICATION_SUBSCRIBER_NOT_FOUND));

        pushNotificationSubscriberRepository.delete(pushNotificationSubscriber);
    }

    @Transactional(readOnly = true)
    public boolean isPushNotificationSubscriber(Member member) {
        return pushNotificationSubscriberRepository.existsByMember(member);
    }
}
