package site.packit.packit.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.packit.packit.domain.auth.dto.AuthenticationTokens;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.global.util.CookieUtil;
import site.packit.packit.global.util.HeaderUtil;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.REQUEST_REFRESH_TOKEN_NOT_FOUND;

@Service
public class WebAuthService {

    @Value("${app.cookie.refresh-token-cookie-name}")
    private String refreshTokenCookieName;
    @Value("${app.jwt.expiry.refresh-token-cookie-max-age}")
    private int refreshTokenCookieMaxAge;

    private final TokenService tokenService;

    public WebAuthService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, String memberPersonalId) {
        tokenService.deleteMemberRefreshToken(memberPersonalId);
        CookieUtil.deleteCookie(request, response, refreshTokenCookieName);
    }

    public String reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String expiredAccessTokenValue = HeaderUtil.getAccessToken(request);
        String refreshTokenValue = parseRefreshTokenFromRequest(request);

        AuthenticationTokens authenticationTokens = tokenService.reissueToken(expiredAccessTokenValue, refreshTokenValue);
        setRefreshTokenToCookie(request, response, authenticationTokens.refreshTokens());

        return authenticationTokens.accessToken();
    }

    private String parseRefreshTokenFromRequest(HttpServletRequest request) {
        return CookieUtil.getCookie(request, refreshTokenCookieName)
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthException(REQUEST_REFRESH_TOKEN_NOT_FOUND));
    }

    private void setRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        CookieUtil.deleteCookie(request, response, refreshTokenCookieName);
        CookieUtil.addCookie(response, refreshTokenCookieName, refreshToken, refreshTokenCookieMaxAge);
    }
}
