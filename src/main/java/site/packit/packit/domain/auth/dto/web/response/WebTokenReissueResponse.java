package site.packit.packit.domain.auth.dto.web.response;

public record WebTokenReissueResponse(String accessToken) {

    public static WebTokenReissueResponse of(String accessToken) {
        return new WebTokenReissueResponse(accessToken);
    }
}
