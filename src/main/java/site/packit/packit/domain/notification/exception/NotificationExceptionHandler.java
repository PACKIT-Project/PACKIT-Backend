package site.packit.packit.domain.notification.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.packit.packit.global.exception.ErrorCode;
import site.packit.packit.global.response.error.ErrorApiResponse;

@Slf4j
@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ErrorApiResponse> handleNotificationException(NotificationException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.error("[handle NotificationException] - {}", exception.getMessage());

        return new ResponseEntity<>(
                ErrorApiResponse.of(errorCode, errorCode.getMessage()),
                errorCode.getHttpStatus()
        );
    }
}
