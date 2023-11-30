package site.packit.packit.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.member.dto.business.MemberDto;
import site.packit.packit.domain.member.dto.request.UpdateMemberProfileRequest;
import site.packit.packit.domain.member.dto.response.GetMemberProfileResponse;
import site.packit.packit.domain.member.dto.response.RegisterResponse;
import site.packit.packit.domain.member.service.MemberService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

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
            @RequestBody UpdateMemberProfileRequest request
    ) {
        Long registerMemberId = memberService.register(userPrincipal.getUsername(), request);

        return successApiResponse(OK, "성공적으로 회원가입되었습니다.", RegisterResponse.of(registerMemberId));
    }

    @GetMapping("/profiles")
    public ResponseEntity<SingleSuccessApiResponse<GetMemberProfileResponse>> getMemberProfile(@AuthenticationPrincipal CustomUserPrincipal principal) {
        MemberDto memberDto = memberService.getMember(principal.getMemberId());
        GetMemberProfileResponse memberProfile = GetMemberProfileResponse.of(memberDto);

        return ResponseUtil.successApiResponse(OK, "성공적으로 사용자 프로필 정보가 조회되었습니다.", memberProfile);
    }

    @PutMapping("/profiles")
    public ResponseEntity<SuccessApiResponse> updateMemberProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody UpdateMemberProfileRequest request
    ) {
        memberService.updateMemberProfile(principal.getMemberId(), request);

        return ResponseUtil.successApiResponse(OK, "성공적으로 사용자 프로필이 업데이트되었습니다.");
    }

    @DeleteMapping()
    public ResponseEntity<SuccessApiResponse> deleteMember(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        memberService.deleteMember(principal.getMemberId());

        return ResponseUtil.successApiResponse(OK, "성공적으로 사용자 정보가 삭제되었습니다.");
    }
}
