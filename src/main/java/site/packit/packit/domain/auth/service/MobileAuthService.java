package site.packit.packit.domain.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.auth.dto.AuthenticationTokens;
import site.packit.packit.domain.auth.dto.mobile.request.MobileLoginRequest;
import site.packit.packit.domain.auth.dto.mobile.request.MobileTokenReissueRequest;
import site.packit.packit.domain.auth.dto.mobile.response.MobileLoginResponse;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.service.MemberService;

import static site.packit.packit.domain.member.constant.AccountStatus.DELETE;
import static site.packit.packit.global.util.LoginResponseUtil.createActiveMemberOrWaitingToJoinLoginResponse;
import static site.packit.packit.global.util.LoginResponseUtil.createDeleteMemberLoginResponse;

@Slf4j
@Service
public class MobileAuthService {

    private final TokenService tokenService;
    private final MemberService memberService;

    public MobileAuthService(TokenService tokenService, MemberService memberService) {
        this.tokenService = tokenService;
        this.memberService = memberService;
    }

    @Transactional
    public MobileLoginResponse login(MobileLoginRequest request) {
        Member member = memberService.findMemberByPersonalIdOrCreateMember(request.memberPersonalId(), request.getLoginProvider());
        CustomUserPrincipal userPrincipal = CustomUserPrincipal.from(member);

        if (userPrincipal.getMemberAccountStatus() == DELETE) {
            return createDeleteMemberLoginResponse(userPrincipal.getMemberAccountStatus());
        }

        String accessToken = tokenService.createAccessToken(userPrincipal);
        log.info("[created-access-token] : " + accessToken);
        String refreshToken = tokenService.createRefreshToken(userPrincipal);

        return createActiveMemberOrWaitingToJoinLoginResponse(userPrincipal.getMemberAccountStatus(), accessToken, refreshToken);
    }

    public void logout(String memberPersonalId) {
        tokenService.deleteMemberRefreshToken(memberPersonalId);
    }

    public AuthenticationTokens reissueToken(MobileTokenReissueRequest request) {
        return tokenService.reissueToken(request.expiredAccessToken(), request.refreshToken());
    }
}
