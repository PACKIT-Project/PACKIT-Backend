package site.packit.packit.domain.notification.exception;

import site.packit.packit.global.exception.ErrorCode;

public class NotificationException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotificationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
