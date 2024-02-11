package site.packit.packit.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.global.audit.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FcmToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String value;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    public FcmToken(String value, Member member) {
        this.value = value;
        this.member = member;
    }

    public void updateValue(String value) {
        this.value = value;
    }
}
