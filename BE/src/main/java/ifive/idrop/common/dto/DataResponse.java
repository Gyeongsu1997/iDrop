package ifive.idrop.common.dto;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class DataResponse<T> extends BaseResponse {
    private static final int successCode = 0;
    private static final String successMessage = "OK";

    private final T data;

    private DataResponse(T data) {
        super(successCode, successMessage);
        this.data = data;
    }

    public static DataResponse<?> success() {
        return new DataResponse<>(new HashMap<>());
    }

    public static <T> DataResponse<T> of(T data) {
        return new DataResponse<>(data);
    }
}
