package site.packit.packit.global.response.success;

import lombok.Getter;

@Getter
public class SingleSuccessApiResponse<T> extends SuccessApiResponse {

    private final T data;

    private SingleSuccessApiResponse(String message, T data) {
        super(message);
        this.data = data;
    }

    public static <T> SingleSuccessApiResponse<T> of(String message, T data) {
        return new SingleSuccessApiResponse<>(message, data);
    }
}
