package ifive.idrop.common.enums;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public static class Converter implements AttributeConverter<Day, String> {
        @Override
        public String convertToDatabaseColumn(Day attribute) {
            return attribute.getValue();
        }

        @Override
        public Day convertToEntityAttribute(String dbData) {
            return Day.of(dbData);
        }
    }
}
