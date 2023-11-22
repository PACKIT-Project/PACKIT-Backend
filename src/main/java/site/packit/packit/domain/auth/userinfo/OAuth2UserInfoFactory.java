package site.packit.packit.domain.auth.userinfo;

import site.packit.packit.domain.member.constant.LoginProvider;
import site.packit.packit.domain.auth.exception.AuthException;

import java.util.Map;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.INVALID_LOGIN_PROVIDER;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuthUserInfo(
            LoginProvider loginProvider,
            Map<String, Object> attributes
    ) {
        return switch (loginProvider) {
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            default -> throw new AuthException(INVALID_LOGIN_PROVIDER);
        };
    }
}
