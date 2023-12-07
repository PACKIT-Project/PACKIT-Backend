package site.packit.packit.domain.auth.dto.mobile.response;

public record MobileLoginResponse(String memberStatus, String accessToken, String refreshToken) {

}
