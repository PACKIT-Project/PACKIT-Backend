package site.packit.packit.domain.travel.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum TravelErrorCode implements ErrorCode {

    TRAVEL_NOT_FOUND("TR-C-001", NOT_FOUND, "존재하지 않는 여행입니다."),
    TRAVEL_NOT_EDIT("TR-C-002", NOT_FOUND, "사용자가 생성한 여행이 아닙니다."),
    DESTINATION_NOT_FOUND("TR-C-003", NOT_FOUND, "존재하지 않는 여행지입니다."),
    NOT_MEMBER_IN("TR-C-004", FORBIDDEN, "해당 여행의 멤버가 아닙니다."),
    INVITATION_NOT_FOUND("TR-C-005", NOT_FOUND, "유효하지 않은 코드입니다."),
    EXISTS_MEMBER_IN("TR-C-006", CONFLICT, "이미 참여한 여행입니다."),
    MAX_PARTICIPANTS_EXCEEDED("TR-C-007", BAD_REQUEST, "최대 인원을 초과했습니다.");

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