package site.packit.packit.global.response.error;

import lombok.Getter;
import site.packit.packit.global.exception.ErrorCode;
import site.packit.packit.global.util.GsonUtil;

import java.time.LocalDateTime;

@Getter
public class ErrorApiResponse {

    private final String errorCode;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    private ErrorApiResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ErrorApiResponse of(ErrorCode errorCode) {
        return new ErrorApiResponse(errorCode.getCode(), errorCode.getMessage());
    }

    public static ErrorApiResponse of(ErrorCode errorCode, String message) {
        return new ErrorApiResponse(errorCode.getCode(), message);
    }

    public String convertToJson() {
        return GsonUtil.toJson(this);
    }
}
