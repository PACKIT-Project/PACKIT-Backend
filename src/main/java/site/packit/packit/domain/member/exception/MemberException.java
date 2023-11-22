package site.packit.packit.domain.member.exception;

import site.packit.packit.global.exception.ErrorCode;

public class MemberException extends RuntimeException {

    private final ErrorCode errorCode;

    public MemberException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
