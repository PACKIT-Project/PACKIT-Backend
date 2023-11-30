package site.packit.packit.domain.auth.dto.request;

import site.packit.packit.domain.member.constant.LoginProvider;

public record LoginRequest(String memberPersonalId, String loginProvider) {

    public LoginProvider getLoginProvider() {
        return LoginProvider.valueOf(loginProvider.toUpperCase());
    }
}
