package site.packit.packit.domain.auth.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.auth.dto.AuthenticationTokens;
import site.packit.packit.domain.auth.entity.RefreshToken;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.auth.jwt.AuthenticationToken;
import site.packit.packit.domain.auth.jwt.TokenProvider;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.repository.RefreshTokenRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.exception.MemberException;
import site.packit.packit.domain.member.repository.MemberRepository;
import site.packit.packit.global.util.CookieUtil;
import site.packit.packit.global.util.HeaderUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.INVALID_TOKEN;
import static site.packit.packit.domain.auth.exception.AuthErrorCode.NOT_EXPIRED_TOKEN;
import static site.packit.packit.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

@Service
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Value("${app.cookie.refresh-token-cookie-name}")
    private String refreshTokenCookieName;

    public AuthService(
            RefreshTokenRepository refreshTokenRepository,
            MemberRepository memberRepository,
            TokenProvider tokenProvider
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
    }

    public Authentication convertAccessTokenToAuthentication(AuthenticationToken accessToken) {
        accessToken.validate();
        UserDetails userDetails = createUserDetails(accessToken);

        return new UsernamePasswordAuthenticationToken(userDetails, accessToken, userDetails.getAuthorities());
    }

    private UserDetails createUserDetails(AuthenticationToken accessToken) {
        Claims claims = accessToken.getClaims();
        Collection<? extends GrantedAuthority> roles = parseMemberAuthorityFromClaims(claims);
        Member member = memberRepository.findByPersonalId(claims.getSubject())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        return CustomUserPrincipal.from(member, roles);
    }

    private Collection<? extends GrantedAuthority> parseMemberAuthorityFromClaims(Claims claims) {
        String roles = claims.get("roles", String.class);

        return Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AuthenticationTokens reissueToken(String expiredAccessTokenValue, String refreshTokenValue) {
        AuthenticationToken expiredAccessToken = tokenProvider.convertAccessTokenValueToObject(expiredAccessTokenValue);
        validateExpiredToken(expiredAccessToken);

        AuthenticationToken refreshToken = tokenProvider.convertRefreshTokenValueToObject(refreshTokenValue);
        validateRefreshToken(refreshToken);

        String memberPersonalId = refreshToken.getClaims().getSubject();
        Collection<? extends GrantedAuthority> roles = parseMemberAuthorityFromClaims(refreshToken.getClaims());

        return new AuthenticationTokens(
                tokenProvider.createAccessToken(memberPersonalId, roles),
                createNewRefreshToken(refreshTokenValue, memberPersonalId, roles)
        );
    }

    private void validateExpiredToken(AuthenticationToken expiredAccessToken) {
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

    private AuthenticationToken createNewRefreshToken(
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

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationToken accessToken = tokenProvider.convertAccessTokenValueToObject(HeaderUtil.getAccessToken(request));
        refreshTokenRepository.deleteAllByMemberPersonalId(accessToken.getClaims().getSubject());
        CookieUtil.deleteCookie(request, response, refreshTokenCookieName);
    }
}
