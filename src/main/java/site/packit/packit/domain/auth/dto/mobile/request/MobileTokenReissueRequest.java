package site.packit.packit.domain.auth.dto.mobile.request;

public record MobileTokenReissueRequest(String expiredAccessToken, String refreshToken) {
}
