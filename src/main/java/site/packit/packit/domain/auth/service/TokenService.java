package site.packit.packit.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.auth.dto.AuthenticationTokens;
import site.packit.packit.domain.auth.entity.RefreshToken;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.auth.jwt.AuthenticationToken;
import site.packit.packit.domain.auth.jwt.TokenProvider;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.repository.RefreshTokenRepository;
import site.packit.packit.domain.auth.userinfo.OAuth2UserInfo;
import site.packit.packit.domain.auth.userinfo.OAuth2UserInfoFactory;
import site.packit.packit.global.util.HeaderUtil;

import java.util.Collection;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.INVALID_TOKEN;
import static site.packit.packit.domain.auth.exception.AuthErrorCode.NOT_EXPIRED_TOKEN;

@Transactional
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(RefreshTokenRepository refreshTokenRepository, TokenProvider tokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
    }

    public String createAccessToken(CustomUserPrincipal userPrincipal) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuthUserInfo(userPrincipal.getLoginProvider(), userPrincipal.getAttributes());
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        return tokenProvider.createAccessToken(oAuth2UserInfo.getOpenId(), authorities).getValue();
    }

    public String createRefreshToken(CustomUserPrincipal userPrincipal) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuthUserInfo(userPrincipal.getLoginProvider(), userPrincipal.getAttributes());
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        String refreshToken = tokenProvider.createRefreshToken(oAuth2UserInfo.getOpenId(), authorities).getValue();

        refreshTokenRepository.saveAndFlush(RefreshToken.of(refreshToken, oAuth2UserInfo.getOpenId()));

        return refreshToken;
    }

    public AuthenticationTokens reissueToken(String expiredAccessTokenValue, String refreshTokenValue) {
        AuthenticationToken expiredAccessToken = tokenProvider.convertAccessTokenValueToObject(expiredAccessTokenValue);
        checkExpiredToken(expiredAccessToken);

        AuthenticationToken refreshToken = tokenProvider.convertRefreshTokenValueToObject(refreshTokenValue);
        validateRefreshToken(refreshToken);

        AuthenticationToken newAccessToken = tokenProvider.createAccessToken(refreshToken.getSubject(), refreshToken.getMemberGrantedAuthorities());
        AuthenticationToken newRefreshToken = updateNewRefreshToken(refreshTokenValue, refreshToken.getSubject(), refreshToken.getMemberGrantedAuthorities());

        return new AuthenticationTokens(newAccessToken, newRefreshToken);
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

    public void deleteAllRefreshToken(HttpServletRequest request) {
        AuthenticationToken accessToken = parseAccessTokenFromRequest(request);
        refreshTokenRepository.deleteAllByMemberPersonalId(accessToken.getSubject());
    }

    private AuthenticationToken parseAccessTokenFromRequest(HttpServletRequest request) {
        return tokenProvider.convertAccessTokenValueToObject(HeaderUtil.getAccessToken(request));
    }
}
