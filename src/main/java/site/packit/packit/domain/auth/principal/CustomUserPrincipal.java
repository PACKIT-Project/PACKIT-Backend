package site.packit.packit.domain.auth.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import site.packit.packit.domain.member.constant.AccountStatus;
import site.packit.packit.domain.member.constant.LoginProvider;
import site.packit.packit.domain.member.entity.Member;

import java.util.Collection;
import java.util.Map;

import static site.packit.packit.domain.member.constant.AccountStatus.ACTIVE;

public class CustomUserPrincipal implements UserDetails, OAuth2User, OidcUser {

    private final Long memberId;
    private final String memberPersonalId;
    private final AccountStatus memberAccountStatus;
    private final LoginProvider loginProvider;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> oAuth2UserInfoAttributes;

    private CustomUserPrincipal(
            Long memberId,
            String memberPersonalId,
            AccountStatus memberAccountStatus,
            LoginProvider loginProvider,
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> oAuth2UserInfoAttributes
    ) {
        this.memberId = memberId;
        this.memberPersonalId = memberPersonalId;
        this.memberAccountStatus = memberAccountStatus;
        this.loginProvider = loginProvider;
        this.authorities = authorities;
        this.oAuth2UserInfoAttributes = oAuth2UserInfoAttributes;
    }

    public static CustomUserPrincipal from(Member member) {
        return from(member, Map.of());
    }

    public static CustomUserPrincipal from(
            Member member,
            Map<String, Object> oAuth2UserInfo
    ) {
        return new CustomUserPrincipal(
                member.getId(),
                member.getPersonalId(),
                member.getAccountStatus(),
                member.getLoginProvider(),
                member.getGrantedAuthorities(),
                oAuth2UserInfo
        );
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfoAttributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getName() {
        return memberPersonalId;
    }

    @Override
    public String getUsername() {
        return memberPersonalId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.memberAccountStatus == ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.memberAccountStatus == ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.memberAccountStatus == ACTIVE;
    }

    @Override
    public boolean isEnabled() {
        return this.memberAccountStatus == ACTIVE;
    }

    @Override
    public Map<String, Object> getClaims() {
        return Map.of();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [");
        sb.append("memberStatus=").append(this.memberAccountStatus).append(", ");
        sb.append("Granted Authorities=").append(this.authorities).append("], ");
        sb.append("oAuth2UserInfoAttributes=[PROTECTED]");

        return sb.toString();
    }

    public Long getMemberId() {
        return memberId;
    }

    public AccountStatus getMemberAccountStatus() {
        return memberAccountStatus;
    }

    public LoginProvider getLoginProvider() {
        return loginProvider;
    }
}
