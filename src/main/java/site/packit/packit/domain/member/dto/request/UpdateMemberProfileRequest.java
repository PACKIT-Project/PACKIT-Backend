package site.packit.packit.domain.member.dto.request;

public record UpdateMemberProfileRequest(
        String nickname,
        String profileImageUrl,
        boolean enableNotification,
        boolean checkTerms
) {
}
