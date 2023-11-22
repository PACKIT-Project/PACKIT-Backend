package site.packit.packit.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class TokenProvider {

    private final SecretKey accessTokenSecretKey;
    private final long accessTokenExpirationDate;
    private final SecretKey refreshTokenSecretKey;
    private final long refreshTokenExpirationDate;

    public TokenProvider(
            String accessTokenSecretKeyValue,
            long accessTokenExpirationDateValue,
            String refreshTokenSecretKeyValue,
            long refreshTokenExpirationDateValue
    ) {
        this.accessTokenSecretKey = convertTokenSecretKeyValueToObject(accessTokenSecretKeyValue);
        this.accessTokenExpirationDate = accessTokenExpirationDateValue;
        this.refreshTokenSecretKey = convertTokenSecretKeyValueToObject(refreshTokenSecretKeyValue);
        this.refreshTokenExpirationDate = refreshTokenExpirationDateValue;
    }

    private SecretKey convertTokenSecretKeyValueToObject(String tokenSecretKeyValue) {
        return Keys.hmacShaKeyFor(tokenSecretKeyValue.getBytes(StandardCharsets.UTF_8));
    }

    public AuthenticationToken createAccessToken(String memberPersonalId, Collection<? extends GrantedAuthority> memberAccountRoles) {
        return new AuthenticationToken(
                accessTokenSecretKey,
                createTokenValue(memberPersonalId, memberAccountRoles, accessTokenExpirationDate, accessTokenSecretKey)
        );
    }

    private String createTokenValue(
            String memberPersonalId,
            Collection<? extends GrantedAuthority> memberAccountRoles,
            long expirationDate,
            SecretKey key
    ) {
        String roles = convertMemberAccountRolesToString(memberAccountRoles);
        Claims claim = createJwtClaims(memberPersonalId, roles, expirationDate);

        return Jwts.builder()
                .claims(claim)
                .signWith(key)
                .compact();
    }

    private String convertMemberAccountRolesToString(Collection<? extends GrantedAuthority> memberAccountRoles) {
        return memberAccountRoles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private Claims createJwtClaims(
            String memberPersonalId,
            String roles,
            long expirationDate
    ) {
        return Jwts.claims()
                .subject(memberPersonalId)
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(convertTokenExpirationDateValueToObject(expirationDate))
                .add("roles", roles)
                .build();
    }

    private Date convertTokenExpirationDateValueToObject(long tokenExpirationDateValue) {
        return new Date(new Date().getTime() + tokenExpirationDateValue);
    }

    public AuthenticationToken createRefreshToken(String memberPersonalId, Collection<? extends GrantedAuthority> memberAccountRoles) {
        return new AuthenticationToken(
                refreshTokenSecretKey,
                createTokenValue(memberPersonalId, memberAccountRoles, refreshTokenExpirationDate, refreshTokenSecretKey)
        );
    }

    public AuthenticationToken convertAccessTokenValueToObject(String accessTokenValue) {
        return new AuthenticationToken(accessTokenSecretKey, accessTokenValue);
    }

    public AuthenticationToken convertRefreshTokenValueToObject(String refreshTokenValue) {
        return new AuthenticationToken(refreshTokenSecretKey, refreshTokenValue);
    }
}
