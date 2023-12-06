package site.packit.packit.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.auth.dto.AuthenticationTokens;
import site.packit.packit.domain.auth.dto.mobile.request.MobileLoginRequest;
import site.packit.packit.domain.auth.dto.mobile.request.MobileTokenReissueRequest;
import site.packit.packit.domain.auth.dto.mobile.response.MobileLoginResponse;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.service.MemberService;
import site.packit.packit.global.util.LoginResponseUtil;

import static site.packit.packit.domain.member.constant.AccountStatus.ACTIVE;
import static site.packit.packit.domain.member.constant.AccountStatus.WAITING_TO_JOIN;

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

        if (userPrincipal.getMemberAccountStatus() == ACTIVE) {
            String accessToken = tokenService.createAccessToken(userPrincipal);
            String refreshToken = tokenService.createRefreshToken(userPrincipal);

            return LoginResponseUtil.createActiveMemberLoginResponse(userPrincipal.getMemberAccountStatus(), accessToken, refreshToken);
        }

        if (userPrincipal.getMemberAccountStatus() == WAITING_TO_JOIN) {
            return LoginResponseUtil.createWaitingToJoinMemberLoginResponse(userPrincipal.getMemberAccountStatus(), userPrincipal.getUsername());
        }

        return LoginResponseUtil.createDeleteMemberLoginResponse(userPrincipal.getMemberAccountStatus());
    }

    public void logout(String memberPersonalId) {
        tokenService.deleteMemberRefreshToken(memberPersonalId);
    }

    public AuthenticationTokens reissueToken(MobileTokenReissueRequest request) {
        return tokenService.reissueToken(request.expiredAccessToken(), request.refreshToken());
    }
}
