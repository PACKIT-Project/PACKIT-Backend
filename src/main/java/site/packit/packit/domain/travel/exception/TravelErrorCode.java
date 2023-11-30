package site.packit.packit.domain.travel.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum TravelErrorCode implements ErrorCode {

    TRAVEL_NOT_FOUND("TR-C-001", NOT_FOUND, "존재하지 않는 여행입니다."),
    TRAVEL_NOT_EDIT("TR-C-002", NOT_FOUND, "사용자가 생성한 여행이 아닙니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    TravelErrorCode(
            String code,
            HttpStatus httpStatus,
            String message
    ) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}