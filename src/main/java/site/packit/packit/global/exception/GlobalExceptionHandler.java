package site.packit.packit.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.packit.packit.global.response.error.ErrorApiResponse;

import static site.packit.packit.global.exception.GlobalErrorCode.INVALID_REQUEST_METHOD;
import static site.packit.packit.global.exception.GlobalErrorCode.SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ErrorApiResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error("[handle HttpRequestMethodNotSupportedException] - {}", INVALID_REQUEST_METHOD.getMessage());

        return new ResponseEntity<>(ErrorApiResponse.of(INVALID_REQUEST_METHOD), INVALID_REQUEST_METHOD.getHttpStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorApiResponse> handleException(Exception exception) {
        log.error("[handle Exception] - {}", exception.getMessage());

        return new ResponseEntity<>(ErrorApiResponse.of(SERVER_ERROR), SERVER_ERROR.getHttpStatus());
    }
}
