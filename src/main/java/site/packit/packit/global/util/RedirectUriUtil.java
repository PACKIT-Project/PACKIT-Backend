package site.packit.packit.global.util;

import org.springframework.web.util.UriComponentsBuilder;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;

import static site.packit.packit.domain.member.constant.AccountStatus.ACTIVE;
import static site.packit.packit.domain.member.constant.AccountStatus.WAITING_TO_JOIN;

public class RedirectUriUtil {

    public static String createRedirectUri(
            String redirectUrl,
            String accessToken,
            CustomUserPrincipal principal
    ) {
        if (principal.getMemberAccountStatus() == ACTIVE) {
            return createRedirectUriForActiveMember(redirectUrl, principal.getMemberAccountStatus().name(), accessToken);
        }

        if (principal.getMemberAccountStatus() == WAITING_TO_JOIN) {
            return createRedirectUriForWaitingToJoinMember(redirectUrl, principal.getMemberAccountStatus().name(), principal.getUsername());
        }

        return createRedirectUriForDeleteMember(redirectUrl, principal.getMemberAccountStatus().name());
    }

    private static String createRedirectUriForActiveMember(
            String redirectUrl,
            String memberStatus,
            String accessToken
    ) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("member-status", memberStatus)
                .queryParam("access-token", accessToken)
                .build()
                .toUriString();
    }

    private static String createRedirectUriForWaitingToJoinMember(
            String redirectUrl,
            String memberStatus,
            String memberPersonalId
    ) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("member-status", memberStatus)
                .queryParam("member-personal-id", memberPersonalId)
                .build()
                .toUriString();
    }

    private static String createRedirectUriForDeleteMember(String redirectUrl, String memberStatus) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("member-status", memberStatus)
                .build()
                .toUriString();
    }
}
