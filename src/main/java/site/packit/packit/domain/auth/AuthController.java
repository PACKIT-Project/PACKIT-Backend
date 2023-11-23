package site.packit.packit.domain.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.packit.packit.domain.auth.dto.request.LoginRequest;
import site.packit.packit.domain.auth.dto.response.LoginResponse;
import site.packit.packit.domain.auth.service.AuthService;
import site.packit.packit.domain.auth.service.TokenService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

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

        return ResponseUtil.successApiResponse(HttpStatus.OK, "성공적으로 로그인 되었습니다.", loginResult);
    }
}
