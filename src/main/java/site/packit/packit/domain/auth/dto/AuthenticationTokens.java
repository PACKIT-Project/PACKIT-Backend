package site.packit.packit.domain.auth.dto;

import site.packit.packit.domain.auth.jwt.AuthenticationToken;

public record AuthenticationTokens(
        AuthenticationToken accessToken,
        AuthenticationToken refreshToken
) {
}
