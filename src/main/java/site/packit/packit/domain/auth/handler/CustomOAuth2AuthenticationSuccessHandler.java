package site.packit.packit.domain.auth.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import site.packit.packit.domain.auth.service.TokenService;
import site.packit.packit.global.util.CookieUtil;
import site.packit.packit.global.util.LoginResponseUtil;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static site.packit.packit.domain.auth.constant.CookieConstant.*;
import static site.packit.packit.domain.auth.exception.AuthErrorCode.INVALID_REDIRECT_URI;
import static site.packit.packit.domain.member.constant.AccountStatus.ACTIVE;
import static site.packit.packit.domain.member.constant.AccountStatus.WAITING_TO_JOIN;

@Slf4j
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;

    @Value("${app.oauth2.authorized-redirect-uris}")
    private List<String> AUTHORIZED_REDIRECT_URIS;
    @Value("${app.jwt.expiry.refresh-token-expiry}")
    private int REFRESH_TOKEN_EXPIRY;

    public CustomOAuth2AuthenticationSuccessHandler(TokenService tokenService, OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository) {
        this.tokenService = tokenService;
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

        return createLoginResponseWithMemberAccountStatus(request, response, redirectUrl, userPrincipal);
    }

    private String parseRedirectUrl(HttpServletRequest request) {
        String redirectUrl = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());

        validateRedirectUri(redirectUrl);

        return redirectUrl;
    }

    private void validateRedirectUri(String redirectUri) {
        URI clientRedirectUri = URI.create(redirectUri);
        boolean isValidate = AUTHORIZED_REDIRECT_URIS.stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);

                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });

        if (!isValidate) {
            throw new AuthException(INVALID_REDIRECT_URI);
        }
    }

    private String createLoginResponseWithMemberAccountStatus(
            HttpServletRequest request,
            HttpServletResponse response,
            String redirectUrl,
            CustomUserPrincipal userPrincipal
    ) {
        if (userPrincipal.getMemberAccountStatus() == ACTIVE) {
            return createActiveMemberLoginResponse(request, response, redirectUrl, userPrincipal);
        }

        if (userPrincipal.getMemberAccountStatus() == WAITING_TO_JOIN) {
            return LoginResponseUtil.createRedirectUriForWaitingToJoinMember(redirectUrl, userPrincipal.getMemberAccountStatus(), userPrincipal.getUsername());
        }

        return LoginResponseUtil.createRedirectUriForDeleteMember(redirectUrl, userPrincipal.getMemberAccountStatus());
    }

    private String createActiveMemberLoginResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String redirectUrl,
            CustomUserPrincipal userPrincipal
    ) {
        String accessToken = tokenService.createAccessToken(userPrincipal);
        String refreshToken = tokenService.createRefreshToken(userPrincipal);

        log.info("[created-access-token] : " + accessToken);

        setCookie(request, response, refreshToken);

        return LoginResponseUtil.createRedirectUriForActiveMember(redirectUrl, userPrincipal.getMemberAccountStatus(), accessToken);
    }

    private void setCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String refreshToken
    ) {
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, REFRESH_TOKEN_EXPIRY);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestBasedOnCookieRepository.removeAuthorizationRequestCookies(request, response);
    }
}
