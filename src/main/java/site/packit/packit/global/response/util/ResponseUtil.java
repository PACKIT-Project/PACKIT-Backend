package site.packit.packit.global.response.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import site.packit.packit.global.response.success.MultipleSuccessApiResponse;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;

import java.util.List;

public class ResponseUtil {

    public static ResponseEntity<SuccessApiResponse> successApiResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
                .body(SuccessApiResponse.of(message));
    }

    public static <T> ResponseEntity<SingleSuccessApiResponse<T>> successApiResponse(HttpStatus httpStatus, String message, T data) {
        return ResponseEntity.status(httpStatus)
                .body(SingleSuccessApiResponse.of(message, data));
    }

    public static <T> ResponseEntity<MultipleSuccessApiResponse<T>> successApiResponse(HttpStatus httpStatus, String message, List<T> data) {
        return ResponseEntity.status(httpStatus)
                .body(MultipleSuccessApiResponse.of(message, data));
    }
}
