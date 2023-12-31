package site.packit.packit.domain.auth.service;

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

    public AuthenticationTokens reissueToken(String expiredAccessTokenValue, String refreshTokenValue) {
        AuthenticationToken expiredAccessToken = tokenProvider.convertAccessTokenValueToObject(expiredAccessTokenValue);
        AuthenticationToken refreshToken = tokenProvider.convertRefreshTokenValueToObject(refreshTokenValue);
        checkExpiredToken(expiredAccessToken);
        validateRefreshToken(refreshToken);

        AuthenticationToken newAccessToken = tokenProvider.createAccessToken(refreshToken.getSubject(), refreshToken.getMemberGrantedAuthorities());
        AuthenticationToken newRefreshToken = updateNewRefreshToken(refreshToken.getValue(), refreshToken.getSubject(), refreshToken.getMemberGrantedAuthorities());

        return new AuthenticationTokens(newAccessToken.getValue(), newRefreshToken.getValue());
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

    public void deleteMemberRefreshToken(String memberPersonalId) {
        refreshTokenRepository.deleteAllByMemberPersonalId(memberPersonalId);
    }

    public AuthenticationToken getAuthenticationToken(String accessTokenValue) {
        return tokenProvider.convertAccessTokenValueToObject(accessTokenValue);
    }
}
