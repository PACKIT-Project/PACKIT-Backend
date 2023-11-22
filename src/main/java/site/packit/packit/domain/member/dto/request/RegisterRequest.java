package site.packit.packit.domain.member.dto.request;

public record RegisterRequest(
        String nickname,
        String profileImageUrl
) {
}
