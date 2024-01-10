package site.packit.packit.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.member.constant.AccountRole;
import site.packit.packit.domain.member.constant.AccountStatus;
import site.packit.packit.domain.member.constant.LoginProvider;
import site.packit.packit.global.audit.BaseTimeEntity;

import java.util.Collection;
import java.util.List;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.LOGIN_PROVIDER_MISMATCH;
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

    @Column(nullable = false)
    private boolean checkTerms;

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
        this.checkTerms = false;
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

    public void register(
            String nickname,
            String profileImageUrl,
            boolean checkTerms
    ) {
        updateMemberProfile(nickname, profileImageUrl);

        this.checkTerms = checkTerms;
        this.accountStatus = ACTIVE;
    }

    public void updateMemberProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public Collection<GrantedAuthority> getGrantedAuthorities() {
        return List.of(new SimpleGrantedAuthority(accountRole.toString()));
    }

    public void remove() {
        this.accountStatus = DELETE;
    }

    public boolean validateLoginProvider(LoginProvider loginProvider) {
        if (this.loginProvider != loginProvider) {
            String errorMessage = "이미 " + this.loginProvider.name() + "계정으로 가입되어 있습니다.";
            throw new AuthException(LOGIN_PROVIDER_MISMATCH, errorMessage);
        }

        return true;
    }
}
