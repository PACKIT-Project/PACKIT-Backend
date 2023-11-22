package site.packit.packit.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.member.dto.request.RegisterRequest;
import site.packit.packit.domain.member.dto.response.RegisterResponse;
import site.packit.packit.domain.member.service.MemberService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;

import static org.springframework.http.HttpStatus.OK;
import static site.packit.packit.global.response.util.ResponseUtil.successApiResponse;

@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<SingleSuccessApiResponse<RegisterResponse>> register(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestBody RegisterRequest request
    ) {
        Long registerMemberId = memberService.register(userPrincipal.getUsername(), request);

        return successApiResponse(OK, "성공적으로 회원가입되었습니다.", RegisterResponse.of(registerMemberId));
    }
}