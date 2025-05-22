package ifive.idrop.common.enums;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE('M'),
    FEMALE('F');

    private final Character value;

    public static Gender of(Character value) {
        return Arrays.stream(Gender.values())
                .filter(g -> g.getValue().equals(value))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static class Converter implements AttributeConverter<Gender, Character> {
        @Override
        public Character convertToDatabaseColumn(Gender attribute) {
            return attribute.getValue();
        }

        @Override
        public Gender convertToEntityAttribute(Character dbData) {
            return Gender.of(dbData);
        }
    }
}
