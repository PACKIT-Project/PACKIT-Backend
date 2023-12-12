package site.packit.packit.domain.destination.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum DestinationErrorCode implements ErrorCode {

    DESTINATION_NOT_FOUND("DT-C-001", NOT_FOUND, "존재하지 않는 여행지입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    DestinationErrorCode(
            String code,
            HttpStatus httpStatus,
            String message
    ) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}