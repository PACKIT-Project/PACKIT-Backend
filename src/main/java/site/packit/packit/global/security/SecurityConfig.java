package site.packit.packit.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import site.packit.packit.domain.auth.exception.CustomAuthenticationEntryPoint;
import site.packit.packit.domain.auth.filter.TokenAuthenticationFilter;
import site.packit.packit.domain.auth.handler.CustomOAuth2AuthenticationFailureHandler;
import site.packit.packit.domain.auth.handler.CustomOAuth2AuthenticationSuccessHandler;
import site.packit.packit.domain.auth.handler.TokenAccessDeniedHandler;
import site.packit.packit.domain.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import site.packit.packit.domain.auth.service.AuthService;
import site.packit.packit.domain.auth.service.TokenService;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class SecurityConfig {

    private static final List<PermitAllPattern> PERMIT_ALL_PATTERNS = List.of(
            PermitAllPattern.of("/api/auth/refresh", POST),
            PermitAllPattern.of("/api/check", GET),
            PermitAllPattern.of("/api/auth/login", POST)
    );

    private final TokenService tokenService;
    private final AuthService authService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    public SecurityConfig(
            TokenService tokenService,
            AuthService authService,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handle -> handle
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(tokenAccessDeniedHandler())
                );
        http
                .oauth2Login(login -> login
                        .authorizationEndpoint(endPoint -> endPoint
                                .baseUri("/api/oauth2/authorization")
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                        )
                        .redirectionEndpoint(endPoint -> endPoint
                                .baseUri("/*/oauth2/code/*")
                        )
                        .userInfoEndpoint(endPoint -> endPoint
                                .userService(oAuth2UserService)
                        )
                        .successHandler(customOAuth2AuthenticationSuccessHandler())
                        .failureHandler(customOAuth2AuthenticationFailureHandler())
                );
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(parseRequestMatchers())
                        .permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    public TokenAccessDeniedHandler tokenAccessDeniedHandler() {
        return new TokenAccessDeniedHandler();
    }

    private TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(authService);
    }

    private AntPathRequestMatcher[] parseRequestMatchers() {
        return PERMIT_ALL_PATTERNS.stream()
                .map(PermitAllPattern::convertAntPathRequestMatcher)
                .toArray(AntPathRequestMatcher[]::new);
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
