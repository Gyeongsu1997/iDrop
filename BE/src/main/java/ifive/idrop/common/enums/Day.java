package ifive.idrop.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Day {
    SUN("Sun", "SUNDAY"),
    MON("Mon", "MONDAY"),
    TUE("Tue", "THUESDAY"),
    WED("Wed", "WEDNESDAY"),
    THU("Thu", "THURSDAY"),
    FRI("Fri", "FRIDAY"),
    SAT("Sat", "SATURDAY");

    private final String day;
    private final String fullDate;

    public static String toFullName(String day) {
        for (Day d : Day.values()) {
            if (d.getDay().equalsIgnoreCase(day)) {
                return d.getFullDate();
            }
        }
        return null;
    }

    public static Day getDayEnum(String day) {
        for (Day d : Day.values()) {
            if (d.getDay().equalsIgnoreCase(day)) {
                return d;
            }
        }
        return null;
    }
}

