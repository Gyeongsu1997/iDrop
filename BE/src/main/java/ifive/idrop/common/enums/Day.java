package ifive.idrop.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Day {
    MON("MON"),
    TUE("TUE"),
    WED("WED"),
    THU("THU"),
    FRI("FRI"),
    SAT("SAT"),
    SUN("SUN");

    private final String value;

    public static Day of(String value) {
        return Arrays.stream(Day.values())
                .filter(d -> d.getValue().equals(value))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static Day fromDayOfWeek(DayOfWeek dayOfWeek) {
        return Day.valueOf(dayOfWeek.name().substring(0, 3));
    }
}
