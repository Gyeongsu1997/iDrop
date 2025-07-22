package ifive.idrop.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode.getHttpStatus(),errorCode.getCode(), errorCode.getMessage());
    }
}
