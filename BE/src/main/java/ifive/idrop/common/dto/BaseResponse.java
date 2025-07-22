package ifive.idrop.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class BaseResponse {
    private final int code;
    private final String message;
}
