package site.packit.packit.domain.auth.service;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import site.packit.packit.domain.member.constant.LoginProvider;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.userinfo.OAuth2UserInfo;
import site.packit.packit.domain.auth.userinfo.OAuth2UserInfoFactory;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.repository.MemberRepository;

import java.util.Collection;
import java.util.List;

import static site.packit.packit.domain.member.constant.AccountStatus.ACTIVE;
import static site.packit.packit.domain.member.constant.AccountStatus.WAITING_TO_JOIN;
import static site.packit.packit.domain.auth.exception.AuthErrorCode.LOGIN_PROVIDER_MISMATCH;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return process(userRequest, oAuth2User);
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        LoginProvider loginProvider = parseLoginProvider(userRequest);
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuthUserInfo(loginProvider, oAuth2User.getAttributes());
        Member member = memberRepository.findByPersonalIdAndAccountStatus(oAuth2UserInfo.getOpenId(), ACTIVE)
                .filter(savedMember -> validateLoginProvider(savedMember, loginProvider))
                .orElseGet(() -> createTempMember(oAuth2UserInfo, loginProvider));

        return CustomUserPrincipal.from(member, parseMemberAccountRoles(member), oAuth2User.getAttributes());
    }

    private LoginProvider parseLoginProvider(OAuth2UserRequest userRequest) {
        String loginProviderValue = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase();

        return LoginProvider.valueOf(loginProviderValue);
    }

    private boolean validateLoginProvider(Member member, LoginProvider loginProvider) {
        if (loginProvider != member.getLoginProvider()) {
            String errorMessage = "이미 " + loginProvider.name() + "계정으로 가입되어 있습니다.";
            throw new AuthException(LOGIN_PROVIDER_MISMATCH, errorMessage);
        }

        return true;
    }

    private Member createTempMember(OAuth2UserInfo userInfo, LoginProvider loginProvider) {
        return memberRepository.findByPersonalIdAndAccountStatus(userInfo.getOpenId(), WAITING_TO_JOIN)
                .orElseGet(() -> memberRepository.save(Member.createTempUser(userInfo.getOpenId(), loginProvider)));
    }

    private Collection<GrantedAuthority> parseMemberAccountRoles(Member member) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getAccountRole().toString());

        return List.of(authority);
    }
}
