package site.packit.packit.domain.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import site.packit.packit.domain.auth.jwt.AuthenticationToken;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.service.MemberService;

@Service
public class AuthService {

    private final TokenService tokenService;
    private final MemberService memberService;

    @Value("${app.cookie.refresh-token-cookie-name}")
    private String refreshTokenCookieName;

    public AuthService(TokenService tokenService, MemberService memberService) {
        this.tokenService = tokenService;
        this.memberService = memberService;
    }

    public Authentication createMemberAuthentication(AuthenticationToken accessToken) {
        accessToken.validate();
        UserDetails userDetails = createUserDetails(accessToken);

        return new UsernamePasswordAuthenticationToken(userDetails, accessToken, userDetails.getAuthorities());
    }

    private UserDetails createUserDetails(AuthenticationToken accessToken) {
        Member member = memberService.getMember(accessToken.getSubject());

        return CustomUserPrincipal.from(member);
    }
}
