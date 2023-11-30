package site.packit.packit.global.exception;

public class ResourceNotFoundException
        extends RuntimeException {
    private final ErrorCode errorCode;

    public ResourceNotFoundException(
            final ErrorCode errorCode
    ) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(
            final ErrorCode errorCode,
            Throwable cause
    ) {
        super(
                errorCode.getMessage(),
                cause
        );
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}