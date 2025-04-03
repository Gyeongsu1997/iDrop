package ifive.idrop.common.dto;

import ifive.idrop.common.exception.BusinessException;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private String message;
    private String solution;

    public static ErrorResponse from(BusinessException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.message = e.getMessage();
        errorResponse.solution = e.getSolution();
        return errorResponse;
    }
}
