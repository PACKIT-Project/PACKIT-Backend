package site.packit.packit.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.auth.dto.request.LoginRequest;
import site.packit.packit.domain.auth.dto.response.LoginResponse;
import site.packit.packit.domain.auth.jwt.AuthenticationToken;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.service.MemberService;

@Service
public class AuthService {

    private final TokenService tokenService;
    private final MemberService memberService;

    public AuthService(TokenService tokenService, MemberService memberService) {
        this.tokenService = tokenService;
        this.memberService = memberService;
    }

    public Authentication createMemberAuthentication(String accessTokenValue) {
        AuthenticationToken accessToken = tokenService.getAuthenticationToken(accessTokenValue);
        accessToken.validate();

        UserDetails userDetails = createUserDetails(accessToken);

        return new UsernamePasswordAuthenticationToken(userDetails, accessToken, userDetails.getAuthorities());
    }

    private UserDetails createUserDetails(AuthenticationToken accessToken) {
        Member member = memberService.getMember(accessToken.getSubject());

        return CustomUserPrincipal.from(member);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Member member = memberService.findMemberByPersonalIdOrCreateMember(request.memberPersonalId(), request.getLoginProvider());
        CustomUserPrincipal userPrincipal = CustomUserPrincipal.from(member);

        String accessToken = tokenService.createAccessToken(userPrincipal);
        String refreshToken = tokenService.createRefreshToken(userPrincipal);

        return LoginResponse.of(accessToken, refreshToken, member.getAccountStatus().name());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        tokenService.deleteAllRefreshToken(request, response);
    }
}
