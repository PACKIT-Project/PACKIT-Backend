package site.packit.packit.domain.auth.dto.mobile.response;

import site.packit.packit.domain.auth.dto.AuthenticationTokens;

public record MobileTokenReissueResponse(String accessToken, String refreshToken) {

    public static MobileTokenReissueResponse of(AuthenticationTokens tokens) {
        return new MobileTokenReissueResponse(tokens.accessToken(), tokens.refreshTokens());
    }
}
