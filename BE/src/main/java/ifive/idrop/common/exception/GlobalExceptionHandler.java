package ifive.idrop.common.exception;

import ifive.idrop.common.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<?> handle(BusinessException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.from(e));
    }
}