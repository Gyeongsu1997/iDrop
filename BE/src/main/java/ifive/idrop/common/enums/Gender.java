package ifive.idrop.common.enums;

import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    M("Male"),
    F("Female");

    private final String label;

    public static Gender of(String gender) {
        if ("남성".equals(gender)) {
            return Gender.M;
        }
        if ("여성".equals(gender)) {
            return Gender.F;
        }
        throw new BusinessException(ErrorCode.INVALID_GENDER);
    }
}
