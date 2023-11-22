package site.packit.packit.global.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.packit.packit.domain.auth.jwt.TokenProvider;

@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret.access-token-secret-key}")
    private String accessTokenSecretKey;
    @Value("${app.jwt.expiry.access-token-expiry}")
    private long accessTokenExpiry;
    @Value("${app.jwt.secret.refresh-token-secret-key}")
    private String refreshTokenSecretKey;
    @Value("${app.jwt.expiry.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Bean
    public TokenProvider authTokenProvider() {
        return new TokenProvider(
                accessTokenSecretKey,
                accessTokenExpiry,
                refreshTokenSecretKey,
                refreshTokenExpiry
        );
    }
}
