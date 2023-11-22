package site.packit.packit.domain.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import site.packit.packit.global.response.error.ErrorApiResponse;

import java.io.IOException;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.INVALID_MEMBER_ROLE;

public class TokenAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        setResponse(response);
    }

    private void setResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().print(ErrorApiResponse.of(INVALID_MEMBER_ROLE).convertToJson());
    }
}
