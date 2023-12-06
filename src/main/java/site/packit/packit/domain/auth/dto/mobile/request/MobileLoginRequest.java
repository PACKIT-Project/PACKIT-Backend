package site.packit.packit.domain.auth.dto.mobile.request;

import site.packit.packit.domain.member.constant.LoginProvider;

public record MobileLoginRequest(String memberPersonalId, String loginProvider) {

    public LoginProvider getLoginProvider() {
        return LoginProvider.valueOf(loginProvider.toUpperCase());
    }
}
