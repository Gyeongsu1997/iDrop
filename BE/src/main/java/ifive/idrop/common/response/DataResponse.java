package ifive.idrop.common.response;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class DataResponse<T> extends BaseResponse {
    private static final int SUCCESS_CODE = 0;
    private static final String SUCCESS_MSG = "OK";

    private final T data;

    private DataResponse(int code, String message, T data) {
        super(code, message);
        this.data = data;
    }

    public static DataResponse<?> success() {
        return new DataResponse<>(SUCCESS_CODE, SUCCESS_MSG, new HashMap<>());
    }

    public static <T> DataResponse<T> success(T data) {
        return new DataResponse<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }
}
