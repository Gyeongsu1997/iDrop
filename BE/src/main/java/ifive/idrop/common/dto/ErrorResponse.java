package ifive.idrop.common.dto;

import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;

public class ErrorResponse extends BaseResponse {
    private ErrorResponse(int code, String message) {
        super(code, message);
    }

    public static ErrorResponse from(BusinessException e) {
        return new ErrorResponse(e.getCode(), e.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}
