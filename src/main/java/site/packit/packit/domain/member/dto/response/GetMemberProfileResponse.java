package site.packit.packit.domain.member.dto.response;

import site.packit.packit.domain.member.dto.MemberDto;
import site.packit.packit.domain.notification.dto.NotificationConfigStatus;

public record GetMemberProfileResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        NotificationConfigStatus notificationConfigStatus
) {
    public static GetMemberProfileResponse of(MemberDto memberDto) {
        return new GetMemberProfileResponse(memberDto.id(), memberDto.nickName(), memberDto.profileImageUrl(), memberDto.notificationConfigStatus());
    }
}
