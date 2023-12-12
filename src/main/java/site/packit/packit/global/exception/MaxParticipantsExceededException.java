package site.packit.packit.global.exception;

public class MaxParticipantsExceededException extends RuntimeException {
    private final ErrorCode errorCode;

    public MaxParticipantsExceededException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public MaxParticipantsExceededException(final ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
