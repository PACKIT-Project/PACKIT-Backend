package site.packit.packit.domain.image.exception;

import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.*;

public enum ImageErrorCode implements ErrorCode {
    UPLOAD_IMAGE_NOT_FOUNT("IMG-C-0001", BAD_REQUEST, "요청에 이미지가 존재하지 않습니다."),
    INVALID_UPLOAD_IMAGE_EXTENSION("IMG-C-0002", BAD_REQUEST, "유효하지 않은 업로드 요청 이미지 확장자입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ImageErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
