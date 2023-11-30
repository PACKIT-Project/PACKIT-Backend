package site.packit.packit.domain.auth.dto.response;

public record TokenReissueResponse(String accessToken) {

    public static TokenReissueResponse of(String accessToken) {
        return new TokenReissueResponse(accessToken);
    }
}
