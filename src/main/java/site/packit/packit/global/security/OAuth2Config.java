package site.packit.packit.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.packit.packit.domain.auth.handler.CustomOAuth2AuthenticationFailureHandler;
import site.packit.packit.domain.auth.handler.CustomOAuth2AuthenticationSuccessHandler;
import site.packit.packit.domain.auth.jwt.TokenProvider;
import site.packit.packit.domain.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import site.packit.packit.domain.auth.repository.RefreshTokenRepository;

@Configuration
public class OAuth2Config {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public OAuth2Config(TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    @Bean
    public CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler() {
        return new CustomOAuth2AuthenticationFailureHandler(
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }
}
