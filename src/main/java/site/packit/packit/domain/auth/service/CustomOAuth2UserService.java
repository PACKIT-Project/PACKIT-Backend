package site.packit.packit.domain.auth.service;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.userinfo.OAuth2UserInfo;
import site.packit.packit.domain.auth.userinfo.OAuth2UserInfoFactory;
import site.packit.packit.domain.member.constant.LoginProvider;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.service.MemberService;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;

    public CustomOAuth2UserService(MemberService memberService) {
        this.memberService = memberService;
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
        Member member = memberService.findMemberByPersonalIdOrCreateMember(oAuth2UserInfo.getOpenId(), loginProvider);

        return CustomUserPrincipal.from(member, oAuth2User.getAttributes());
    }

    private LoginProvider parseLoginProvider(OAuth2UserRequest userRequest) {
        String loginProviderValue = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase();

        return LoginProvider.valueOf(loginProviderValue);
    }
}
