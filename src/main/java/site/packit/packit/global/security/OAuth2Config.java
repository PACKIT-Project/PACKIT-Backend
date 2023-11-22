package site.packit.packit.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.packit.packit.domain.auth.handler.CustomOAuth2AuthenticationFailureHandler;
import site.packit.packit.domain.auth.handler.CustomOAuth2AuthenticationSuccessHandler;
import site.packit.packit.domain.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import site.packit.packit.domain.auth.service.TokenService;

@Configuration
public class OAuth2Config {

    private final TokenService tokenService;

    public OAuth2Config(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler(
                tokenService,
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
