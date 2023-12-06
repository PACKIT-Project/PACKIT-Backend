package site.packit.packit.domain.auth.dto.mobile.response;

import lombok.Getter;

@Getter
public record MobileLoginResponse(String memberStatus, String accessToken, String refreshToken, String memberPersonalId) {

}
