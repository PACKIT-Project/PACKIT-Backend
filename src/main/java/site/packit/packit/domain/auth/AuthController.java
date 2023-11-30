package site.packit.packit.domain.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.dto.request.LoginRequest;
import site.packit.packit.domain.auth.dto.response.LoginResponse;
import site.packit.packit.domain.auth.service.AuthService;
import site.packit.packit.domain.auth.service.TokenService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<SingleSuccessApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResult = authService.login(request);

        return ResponseUtil.successApiResponse(OK, "성공적으로 로그인 되었습니다.", loginResult);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<SuccessApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);

        return ResponseUtil.successApiResponse(OK, "성공적으로 로그아웃 되었습니다.");
    }
}
