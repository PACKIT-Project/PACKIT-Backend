package site.packit.packit.domain.member.dto;

import site.packit.packit.domain.member.constant.AccountRole;
import site.packit.packit.domain.member.constant.AccountStatus;
import site.packit.packit.domain.member.constant.LoginProvider;
import site.packit.packit.domain.member.entity.Member;

public record MemberDto(
        Long id,
        String personalId,
        String nickName,
        String profileImageUrl,
        AccountStatus accountStatus,
        AccountRole accountRole,
        LoginProvider loginProvider
) {
    public static MemberDto of(Member member) {
        return new MemberDto(
                member.getId(),
                member.getPersonalId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getAccountStatus(),
                member.getAccountRole(),
                member.getLoginProvider()
        );
    }
}
