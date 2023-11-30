package site.packit.packit.domain.member.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND("MB-C-001", NOT_FOUND, "존재하지 않는 회원입니다."),
    TEMP_MEMBER_NOT_FOUND("MB-C-002", NOT_FOUND, "가입 대기 중인 회원이 없습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    MemberErrorCode(
            String code,
            HttpStatus httpStatus,
            String message
    ) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
