package site.packit.packit.domain.image.exception;

import site.packit.packit.global.exception.ErrorCode;

public class ImageException extends RuntimeException {

    private final ErrorCode errorCode;

    public ImageException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
