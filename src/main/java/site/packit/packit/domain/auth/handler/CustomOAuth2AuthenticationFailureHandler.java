package site.packit.packit.domain.auth.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.util.UriComponentsBuilder;
import site.packit.packit.domain.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import site.packit.packit.global.util.CookieUtil;

import java.io.IOException;

import static site.packit.packit.domain.auth.constant.CookieConstant.REDIRECT_URI_PARAM_COOKIE_NAME;

public class CustomOAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    public CustomOAuth2AuthenticationFailureHandler(OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository) {
        this.authorizationRequestRepository = authorizationRequestRepository;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        String redirectUrl = parseRedirectUrl(request);
        String targetUrl = createTargetUrl(redirectUrl, exception);

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String parseRedirectUrl(HttpServletRequest request) {
        return CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));
    }

    private String createTargetUrl(String redirectUrl, AuthenticationException exception) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build()
                .toUriString();
    }
}
