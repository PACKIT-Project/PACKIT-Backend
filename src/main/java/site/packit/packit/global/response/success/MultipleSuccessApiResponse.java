package site.packit.packit.global.response.success;

import lombok.Getter;

import java.util.List;

@Getter
public class MultipleSuccessApiResponse<T> extends SuccessApiResponse{

    private final List<T> data;

    private MultipleSuccessApiResponse(String message, List<T> data) {
        super(message);
        this.data = data;
    }

    public static <T> MultipleSuccessApiResponse<T> of(String message, List<T> data) {
        return new MultipleSuccessApiResponse<>(message, data);
    }
}
