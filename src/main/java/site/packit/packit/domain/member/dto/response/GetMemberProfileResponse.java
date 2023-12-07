package site.packit.packit.domain.member.dto.response;

import site.packit.packit.domain.member.dto.MemberDto;

public record GetMemberProfileResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        boolean enableNotification
) {
    public static GetMemberProfileResponse of(MemberDto memberDto) {
        return new GetMemberProfileResponse(memberDto.id(), memberDto.nickName(), memberDto.profileImageUrl(), memberDto.enableNotification());
    }
}
