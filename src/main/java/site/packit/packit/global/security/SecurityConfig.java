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
import site.packit.packit.domain.auth.jwt.TokenProvider;
import site.packit.packit.domain.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import site.packit.packit.domain.auth.service.AuthService;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class SecurityConfig {

    private static final List<PermitAllPattern> PERMIT_ALL_PATTERNS = List.of(
            PermitAllPattern.of("/api/auth/refresh", POST),
            PermitAllPattern.of("/api/check", GET),
            PermitAllPattern.of("/api/auth/login", POST)
    );

    private final TokenProvider tokenProvider;
    private final AuthService authService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            AuthService authService,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
            OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository,
            CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler,
            CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.authService = authService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2AuthorizationRequestBasedOnCookieRepository = oAuth2AuthorizationRequestBasedOnCookieRepository;
        this.customOAuth2AuthenticationSuccessHandler = customOAuth2AuthenticationSuccessHandler;
        this.customOAuth2AuthenticationFailureHandler = customOAuth2AuthenticationFailureHandler;
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
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository)
                        )
                        .redirectionEndpoint(endPoint -> endPoint
                                .baseUri("/*/oauth2/code/*")
                        )
                        .userInfoEndpoint(endPoint -> endPoint
                                .userService(oAuth2UserService)
                        )
                        .successHandler(customOAuth2AuthenticationSuccessHandler)
                        .failureHandler(customOAuth2AuthenticationFailureHandler)
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
        return new TokenAuthenticationFilter(tokenProvider, authService);
    }

    private AntPathRequestMatcher[] parseRequestMatchers() {
        return PERMIT_ALL_PATTERNS.stream()
                .map(PermitAllPattern::convertAntPathRequestMatcher)
                .toArray(AntPathRequestMatcher[]::new);
    }
}
