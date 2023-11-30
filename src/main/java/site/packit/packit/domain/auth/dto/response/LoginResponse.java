package site.packit.packit.domain.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String memberStatus
) {
    public static LoginResponse of(
            String accessToken,
            String refreshToken,
            String memberStatus
    ) {
        return new LoginResponse(accessToken, refreshToken, memberStatus);
    }
}
