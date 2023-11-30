package site.packit.packit.domain.member.dto.response;

public record RegisterResponse(
        Long memberId
) {

    public static RegisterResponse of(Long memberId) {
        return new RegisterResponse(memberId);
    }
}
