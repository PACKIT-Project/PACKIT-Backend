package site.packit.packit.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.member.constant.AccountRole;
import site.packit.packit.domain.member.constant.AccountStatus;
import site.packit.packit.domain.member.constant.LoginProvider;
import site.packit.packit.global.audit.BaseTimeEntity;

import static site.packit.packit.domain.member.constant.AccountRole.USER;
import static site.packit.packit.domain.member.constant.AccountStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true, updatable = false)
    private String personalId;

    @Column(length = 100, nullable = false, unique = true)
    private String nickname;

    @Column(length = 1000, nullable = false)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private AccountRole accountRole;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false, updatable = false)
    private LoginProvider loginProvider;

    private Member(
            String personalId,
            String nickname,
            String profileImageUrl,
            AccountStatus accountStatus,
            AccountRole accountRole,
            LoginProvider loginProvider
    ) {
        this.personalId = personalId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.accountStatus = accountStatus;
        this.accountRole = accountRole;
        this.loginProvider = loginProvider;
    }

    public static Member createTempUser(String personalId, LoginProvider loginProvider) {
        return new Member(
                personalId,
                "TEMP_MEMBER_" + personalId,
                "TEMP_MEMBER",
                WAITING_TO_JOIN,
                USER,
                loginProvider
        );
    }

    public void register(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.accountStatus = ACTIVE;
    }
}
