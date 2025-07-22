package ifive.idrop.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ScheduleUtils {
    public static final int EXPIRATION = 28; //4주의 유효 기간

    public static LocalDate calculateStartDate(LocalDateTime modifiedDate) {
        if (modifiedDate == null) {
            return LocalDate.now().plusDays(1);
        }
        return modifiedDate.toLocalDate().plusDays(1);
    }

    public static LocalDate calculateEndDate(LocalDateTime modifiedDate) {
        if (modifiedDate == null) {
            return calculateStartDate(null).plusDays(EXPIRATION - 1);
        }
        return modifiedDate.toLocalDate().plusDays(EXPIRATION);
    }
}
