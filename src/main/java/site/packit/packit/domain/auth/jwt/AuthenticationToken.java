package site.packit.packit.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import site.packit.packit.domain.auth.exception.AuthException;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.EXPIRED_TOKEN;
import static site.packit.packit.domain.auth.exception.AuthErrorCode.INVALID_TOKEN;

public class AuthenticationToken {

    private final SecretKey key;
    private final String value;

    public AuthenticationToken(SecretKey key, String value) {
        this.key = key;
        this.value = value;
    }

    public Claims getClaims() {
        return parseTokenClaims().orElseThrow(() -> new AuthException(INVALID_TOKEN));
    }

    private Optional<Claims> parseTokenClaims() {
        try {
            return Optional.of(
                    Jwts.parser()
                            .verifyWith(key)
                            .build()
                            .parseSignedClaims(value)
                            .getPayload()
            );
        } catch (ExpiredJwtException e) {
            throw new AuthException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    public void validate() {
        parseTokenClaims().orElseThrow(() -> new AuthException(INVALID_TOKEN));
    }

    public boolean isExpired() {
        try {
            parseTokenClaims();
            return false;
        } catch (ExpiredJwtException exception) {
            return true;
        }
    }

    public String getValue() {
        return value;
    }

    public String getSubject() {
        return getClaims().getSubject();
    }

    public Collection<GrantedAuthority> getMemberGrantedAuthorities() {
        String roles = getClaims().get("roles", String.class);

        return Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
