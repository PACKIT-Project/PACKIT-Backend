package site.packit.packit.global.util;

import org.springframework.web.util.UriComponentsBuilder;
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
}