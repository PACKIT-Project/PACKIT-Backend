package site.packit.packit.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.dto.AuthenticationTokens;
import site.packit.packit.domain.auth.dto.mobile.request.MobileLoginRequest;
import site.packit.packit.domain.auth.dto.mobile.request.MobileTokenReissueRequest;
import site.packit.packit.domain.auth.dto.mobile.response.MobileLoginResponse;
import site.packit.packit.domain.auth.dto.mobile.response.MobileTokenReissueResponse;
import site.packit.packit.domain.auth.dto.web.response.WebTokenReissueResponse;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.auth.service.MobileAuthService;
import site.packit.packit.domain.auth.service.WebAuthService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static site.packit.packit.global.response.util.ResponseUtil.successApiResponse;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final WebAuthService webAuthService;
    private final MobileAuthService mobileAuthService;

    public AuthController(WebAuthService webAuthService, MobileAuthService mobileAuthService) {
        this.webAuthService = webAuthService;
        this.mobileAuthService = mobileAuthService;
    }

    @PostMapping("/mobile/login")
    public ResponseEntity<SingleSuccessApiResponse<MobileLoginResponse>> login(@RequestBody MobileLoginRequest request) {
        MobileLoginResponse loginResult = mobileAuthService.login(request);

        return successApiResponse(OK, "성공적으로 로그인 되었습니다.", loginResult);
    }

    @DeleteMapping("/mobile/logout")
    public ResponseEntity<SuccessApiResponse> logout(@AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        mobileAuthService.logout(userPrincipal.getUsername());

        return successApiResponse(OK, "성공적으로 로그아웃 되었습니다.");
    }

    @PostMapping("/mobile/refresh")
    public ResponseEntity<SingleSuccessApiResponse<MobileTokenReissueResponse>> reIssueToken(@RequestBody MobileTokenReissueRequest request) {
        AuthenticationTokens authenticationTokens = mobileAuthService.reissueToken(request);
        MobileTokenReissueResponse mobileTokenReissueResponse = MobileTokenReissueResponse.of(authenticationTokens);

        return successApiResponse(CREATED, "성공적으로 토큰이 재발급 되었습니다.", mobileTokenReissueResponse);
    }

    @DeleteMapping("/web/logout")
    public ResponseEntity<SuccessApiResponse> logout(@AuthenticationPrincipal CustomUserPrincipal userPrincipal, HttpServletRequest request, HttpServletResponse response) {
        webAuthService.logout(request, response, userPrincipal.getUsername());

        return successApiResponse(OK, "성공적으로 로그아웃 되었습니다.");
    }

    @PostMapping("/web/refresh")
    public ResponseEntity<SingleSuccessApiResponse<WebTokenReissueResponse>> reIssueToken(HttpServletRequest request, HttpServletResponse response) {
        String newAccessToken = webAuthService.reissueToken(request, response);
        WebTokenReissueResponse webTokenReissueResponse = WebTokenReissueResponse.of(newAccessToken);

        return successApiResponse(CREATED, "성공적으로 토큰이 재발급 되었습니다.", webTokenReissueResponse);
    }
}
