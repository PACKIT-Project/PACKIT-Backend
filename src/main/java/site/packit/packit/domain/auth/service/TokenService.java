package site.packit.packit.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.auth.entity.RefreshToken;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.auth.jwt.AuthenticationToken;
import site.packit.packit.domain.auth.jwt.TokenProvider;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.repository.RefreshTokenRepository;
import site.packit.packit.global.util.CookieUtil;
import site.packit.packit.global.util.HeaderUtil;

import java.util.Collection;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.*;

@Transactional
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.cookie.refresh-token-cookie-name}")
    private String refreshTokenCookieName;
    @Value("${app.jwt.expiry.refresh-token-cookie-max-age}")
    private int refreshTokenCookieMaxAge;
    
    public TokenService(RefreshTokenRepository refreshTokenRepository, TokenProvider tokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
    }

    public String createAccessToken(CustomUserPrincipal userPrincipal) {
        String memberPersonalId = userPrincipal.getUsername();
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        return tokenProvider.createAccessToken(memberPersonalId, authorities).getValue();
    }

    public String createRefreshToken(CustomUserPrincipal userPrincipal) {
        String memberPersonalId = userPrincipal.getUsername();
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        String refreshToken = tokenProvider.createRefreshToken(memberPersonalId, authorities).getValue();

        refreshTokenRepository.deleteAllByMemberPersonalId(memberPersonalId);
        refreshTokenRepository.saveAndFlush(RefreshToken.of(refreshToken, userPrincipal.getUsername()));

        return refreshToken;
    }

    public String reissueToken(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationToken expiredAccessToken = parseAccessTokenFromRequest(request);
        AuthenticationToken refreshToken = parseRefreshTokenFromRequest(request);
        checkExpiredToken(expiredAccessToken);
        validateRefreshToken(refreshToken);

        AuthenticationToken newAccessToken = tokenProvider.createAccessToken(refreshToken.getSubject(), refreshToken.getMemberGrantedAuthorities());
        AuthenticationToken newRefreshToken = updateNewRefreshToken(refreshToken.getValue(), refreshToken.getSubject(), refreshToken.getMemberGrantedAuthorities());

        setRefreshTokenToCookie(request, response, newRefreshToken.getValue());

        return newAccessToken.getValue();
    }

    private AuthenticationToken parseRefreshTokenFromRequest(HttpServletRequest request) {
        String refreshTokenValue = CookieUtil.getCookie(request, refreshTokenCookieName)
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthException(REQUEST_REFRESH_TOKEN_NOT_FOUND));

        return tokenProvider.convertRefreshTokenValueToObject(refreshTokenValue);
    }

    private void checkExpiredToken(AuthenticationToken expiredAccessToken) {
        if (!expiredAccessToken.isExpired()) {
            throw new AuthException(NOT_EXPIRED_TOKEN);
        }
    }

    private void validateRefreshToken(AuthenticationToken refreshToken) {
        refreshToken.validate();

        if (!refreshTokenRepository.existsByValue(refreshToken.getValue())) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    private AuthenticationToken updateNewRefreshToken(
            String refreshTokenValue,
            String memberPersonalId,
            Collection<? extends GrantedAuthority> roles
    ) {
        AuthenticationToken newRefreshToken = tokenProvider.createRefreshToken(memberPersonalId, roles);
        RefreshToken storedRefreshToken = refreshTokenRepository.findByValue(refreshTokenValue)
                .orElseThrow(() -> new AuthException(INVALID_TOKEN));

        storedRefreshToken.updateValue(newRefreshToken.getValue());

        return newRefreshToken;
    }

    private void setRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        CookieUtil.deleteCookie(request, response, refreshTokenCookieName);
        CookieUtil.addCookie(response, refreshTokenCookieName, refreshToken, refreshTokenCookieMaxAge);
    }

    public void deleteAllRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationToken accessToken = parseAccessTokenFromRequest(request);
        refreshTokenRepository.deleteAllByMemberPersonalId(accessToken.getSubject());
        CookieUtil.deleteCookie(request, response, refreshTokenCookieName);
    }

    private AuthenticationToken parseAccessTokenFromRequest(HttpServletRequest request) {
        return tokenProvider.convertAccessTokenValueToObject(HeaderUtil.getAccessToken(request));
    }

    public AuthenticationToken getAuthenticationToken(String accessTokenValue) {
        return tokenProvider.convertAccessTokenValueToObject(accessTokenValue);
    }
}
