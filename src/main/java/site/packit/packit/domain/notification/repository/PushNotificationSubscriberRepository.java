package site.packit.packit.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.notification.entity.PushNotificationSubscriber;

import java.util.Optional;

public interface PushNotificationSubscriberRepository extends JpaRepository<PushNotificationSubscriber, Long> {

    Optional<PushNotificationSubscriber> findByMember(Member member);

    boolean existsByMember(Member member);
}
