package site.packit.packit.domain.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import site.packit.packit.global.exception.ErrorCode;
import site.packit.packit.global.response.error.ErrorApiResponse;

import java.io.IOException;

import static site.packit.packit.domain.auth.exception.AuthErrorCode.AUTHENTICATION_ERROR;
import static site.packit.packit.domain.auth.exception.AuthErrorCode.UNTRUSTED_CREDENTIAL;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode exceptionCode = (ErrorCode) request.getAttribute("exceptionCode");

        if (exceptionCode != null) {
            setResponse(response, exceptionCode);
            return;
        }

        if (authException.getClass() == InsufficientAuthenticationException.class) {
            setResponse(response, UNTRUSTED_CREDENTIAL);
            return;
        }

        log.error("Responding with unauthorized error. Message := {}", authException.getMessage());
        setResponse(response, AUTHENTICATION_ERROR);
    }

    private void setResponse(HttpServletResponse response, ErrorCode exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(ErrorApiResponse.of(exceptionCode).convertToJson());
    }
}
