package site.packit.packit.domain.auth.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;
import site.packit.packit.domain.auth.entity.RefreshToken;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.auth.jwt.TokenProvider;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import site.packit.packit.domain.auth.repository.RefreshTokenRepository;
import site.packit.packit.domain.auth.userinfo.OAuth2UserInfo;
import site.packit.packit.domain.auth.userinfo.OAuth2UserInfoFactory;
import site.packit.packit.global.util.CookieUtil;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.INVALID_REDIRECT_URI;

@Slf4j
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;

    @Value("${app.oauth2.authorized-redirect-uris}")
    private List<String> authorizedRedirectUris;
    @Value("${app.cookie.redirect-uri-param-cookie-name}")
    private String redirectUriParamCookieName;
    @Value("${app.cookie.refresh-token-cookie-name}")
    private String refreshTokenCookieName;
    @Value("${app.oauth2.cookie-max-age}")
    private Integer cookieMaxAge;

    public CustomOAuth2AuthenticationSuccessHandler(
            TokenProvider tokenProvider,
            RefreshTokenRepository refreshTokenRepository,
            OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository
    ) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.oAuth2AuthorizationRequestBasedOnCookieRepository = oAuth2AuthorizationRequestBasedOnCookieRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        if (response.isCommitted()) {
            log.debug("응답이 이미 커밋되어 리다이렉트 할 수 없습니다.");
            return;
        }

        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("[target-url] : " + targetUrl);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String redirectUrl = parseRedirectUrl(request);
        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
        String accessToken = createAccessToken(userPrincipal);
        String refreshToken = createRefreshToken(userPrincipal);
        log.info("[created-access-token] : " + accessToken);

        setCookie(request, response, refreshToken);

        return createTargetUri(redirectUrl, accessToken, userPrincipal.getMemberAccountStatus());
    }

    private String parseRedirectUrl(HttpServletRequest request) {
        String redirectUrl = CookieUtil.getCookie(request, redirectUriParamCookieName)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());

        validateRedirectUri(redirectUrl);

        return redirectUrl;
    }

    private void validateRedirectUri(String redirectUri) {
        URI clientRedirectUri = URI.create(redirectUri);
        boolean isValidate = authorizedRedirectUris.stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);

                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });

        if (!isValidate) {
            throw new AuthException(INVALID_REDIRECT_URI);
        }
    }

    private String createAccessToken(CustomUserPrincipal userPrincipal) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuthUserInfo(userPrincipal.getLoginProvider(), userPrincipal.getAttributes());
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        return tokenProvider.createAccessToken(oAuth2UserInfo.getOpenId(), authorities).getValue();
    }

    private String createRefreshToken(CustomUserPrincipal userPrincipal) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuthUserInfo(userPrincipal.getLoginProvider(), userPrincipal.getAttributes());
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        String refreshToken = tokenProvider.createRefreshToken(oAuth2UserInfo.getOpenId(), authorities).getValue();

        refreshTokenRepository.saveAndFlush(RefreshToken.of(refreshToken, oAuth2UserInfo.getOpenId()));

        return refreshToken;
    }

    private void setCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String refreshToken
    ) {
        CookieUtil.deleteCookie(request, response, refreshTokenCookieName);
        CookieUtil.addCookie(response, refreshTokenCookieName, refreshToken, cookieMaxAge);
    }

    private String createTargetUri(
            String redirectUrl,
            String accessToken,
            String memberStatus
    ) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("access-token", accessToken)
                .queryParam("member-status", memberStatus)
                .build()
                .toUriString();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestBasedOnCookieRepository.removeAuthorizationRequestCookies(request, response);
    }
}
