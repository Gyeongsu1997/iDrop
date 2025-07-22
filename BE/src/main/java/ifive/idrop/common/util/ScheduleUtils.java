package ifive.idrop.common.util;

import ifive.idrop.common.exception.BusinessException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ScheduleUtils {
    public static final int EXPIRATION = 28; //4주의 유효 기간

    public static JSONObject toJSONObject(String schedule) {
        JSONParser parser = new JSONParser();
        JSONObject scheduleJSON = null;
        try {
            scheduleJSON = (JSONObject)parser.parse(schedule);
        } catch (ParseException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, 99, e.getMessage());
        }
        return scheduleJSON;
    }

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
