package site.packit.packit.global.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class PermitAllPattern {

    private final String url;
    private final HttpMethod httpMethod;

    private PermitAllPattern(String url, HttpMethod httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
    }

    public static PermitAllPattern of(String url, HttpMethod httpMethod) {
        return new PermitAllPattern(url, httpMethod);
    }

    public AntPathRequestMatcher convertAntPathRequestMatcher() {
        return new AntPathRequestMatcher(url, httpMethod.name());
    }
}
