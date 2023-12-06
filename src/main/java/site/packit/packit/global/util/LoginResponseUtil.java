package site.packit.packit.global.util;

import org.springframework.web.util.UriComponentsBuilder;
import site.packit.packit.domain.auth.dto.mobile.response.MobileLoginResponse;
import site.packit.packit.domain.member.constant.AccountStatus;

public class LoginResponseUtil {

    public static String createRedirectUriForActiveMember(
            String redirectUrl,
            AccountStatus accountStatus,
            String accessToken
    ) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("member-status", accountStatus.name())
                .queryParam("access-token", accessToken)
                .build()
                .toUriString();
    }

    public static String createRedirectUriForWaitingToJoinMember(
            String redirectUrl,
            AccountStatus accountStatus,
            String memberPersonalId
    ) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("member-status", accountStatus.name())
                .queryParam("member-personal-id", memberPersonalId)
                .build()
                .toUriString();
    }

    public static String createRedirectUriForDeleteMember(String redirectUrl, AccountStatus accountStatus) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("member-status", accountStatus.name())
                .build()
                .toUriString();
    }

    public static MobileLoginResponse createActiveMemberLoginResponse(AccountStatus accountStatus, String accessToken, String refreshToken) {
        return new MobileLoginResponse(accountStatus.name(), accessToken, refreshToken, "");
    }

    public static MobileLoginResponse createWaitingToJoinMemberLoginResponse(AccountStatus accountStatus, String memberPersonalId) {
        return new MobileLoginResponse(accountStatus.name(), "", "", memberPersonalId);
    }

    public static MobileLoginResponse createDeleteMemberLoginResponse(AccountStatus accountStatus) {
        return new MobileLoginResponse(accountStatus.name(), "", "", "");
    }
}
