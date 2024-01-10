package site.packit.packit.domain.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum NotificationErrorCode implements ErrorCode {

    PUSH_NOTIFICATION_SUBSCRIBER_NOT_FOUND("NT-C-001", NOT_FOUND, "푸시 알림 구독자 정보가 존재하지 않습니다."),
    PUSH_NOTIFICATION_SUBSCRIBER_ALREADY_EXIST("NT-C-002", BAD_REQUEST, "푸시 알림 구독자 정보가 이미 존재합니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    NotificationErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
