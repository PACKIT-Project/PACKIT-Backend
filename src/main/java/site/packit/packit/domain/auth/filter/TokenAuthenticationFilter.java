package site.packit.packit.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import site.packit.packit.domain.auth.exception.AuthException;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.service.GlobalAuthService;
import site.packit.packit.domain.member.constant.AccountStatus;
import site.packit.packit.global.exception.ErrorCode;
import site.packit.packit.global.util.HeaderUtil;

import java.io.IOException;
import java.util.List;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.*;
import static site.packit.packit.domain.member.constant.AccountStatus.ACTIVE;
import static site.packit.packit.domain.member.constant.AccountStatus.WAITING_TO_JOIN;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> REGISTER_REQUEST_URI = List.of("/api/members", "/api/members/nicknames/is-duplicate", "/api/images");
    private static final List<String> TOKEN_REISSUE_REQUEST_URI = List.of("/api/auth/web/refresh", "/api/auth/mobile/refresh");

    private final GlobalAuthService globalAuthService;

    public TokenAuthenticationFilter(GlobalAuthService globalAuthService) {
        this.globalAuthService = globalAuthService;
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
            Authentication authentication = configAuthentication(accessTokenValue);
            CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
            checkMemberAccountStatus(request, principal.getMemberAccountStatus());
        } catch (AuthException exception) {
            request.setAttribute("exceptionCode", exception.getErrorCode());
            throw new AuthException(exception.getErrorCode());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenReissueRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        return TOKEN_REISSUE_REQUEST_URI.contains(requestURI);
    }

    private Authentication configAuthentication(String accessTokenValue) {
        Authentication authentication = globalAuthService.createMemberAuthentication(accessTokenValue);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    private void checkMemberAccountStatus(HttpServletRequest request, AccountStatus accountStatus) {
        if (!isRegisterRequest(request, accountStatus) && accountStatus != ACTIVE) {
            throw new AuthException(getMemberStatusErrorCode(accountStatus));
        }
    }

    private boolean isRegisterRequest(HttpServletRequest request, AccountStatus accountStatus) {
        String requestURI = request.getRequestURI();

        return REGISTER_REQUEST_URI.contains(requestURI) && accountStatus == WAITING_TO_JOIN;
    }

    private ErrorCode getMemberStatusErrorCode(AccountStatus accountStatus) {
        return (accountStatus == WAITING_TO_JOIN) ? NOT_ACTIVE_MEMBER : DELETE_MEMBER;
    }
}
