package site.packit.packit.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.auth.jwt.AuthenticationToken;
import site.packit.packit.domain.auth.jwt.TokenProvider;
import site.packit.packit.domain.auth.service.AuthService;
import site.packit.packit.global.util.HeaderUtil;

import java.io.IOException;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.REQUEST_TOKEN_NOT_FOUND;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_REISSUE_REQUEST_URI = "/api/auth/refresh";

    private final TokenProvider tokenProvider;
    private final AuthService authService;

    public TokenAuthenticationFilter(TokenProvider tokenProvider, AuthService authService) {
        this.tokenProvider = tokenProvider;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String accessTokenValue = HeaderUtil.getAccessToken(request);

        // 요청에 토큰이 없는 경우
        if (accessTokenValue == null) {
            request.setAttribute("exceptionCode", REQUEST_TOKEN_NOT_FOUND);
            filterChain.doFilter(request, response);
            return;
        }

        // Access Token 재발급 요청인 경우
        if (isTokenReissueRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            setAuthentication(accessTokenValue);
        } catch (AuthException exception) {
            request.setAttribute("exceptionCode", exception.getErrorCode());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenReissueRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        return TOKEN_REISSUE_REQUEST_URI.equals(requestURI);
    }

    private void setAuthentication(String accessTokenValue) {
        AuthenticationToken accessToken = tokenProvider.convertAccessTokenValueToObject(accessTokenValue);
        accessToken.validate();

        Authentication authentication = authService.convertAccessTokenToAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
