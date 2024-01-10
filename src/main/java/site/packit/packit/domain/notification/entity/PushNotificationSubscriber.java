package site.packit.packit.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.global.audit.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PushNotificationSubscriber extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    public PushNotificationSubscriber(Member member) {
        this.member = member;
    }
}
