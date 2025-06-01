package ifive.idrop.common.enums;

import ifive.idrop.common.enums.converter.AbstractEnumAttributeConverter;
import ifive.idrop.common.enums.converter.BaseEnumAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender implements BaseEnumAttribute<Character> {
    MALE('M'),
    FEMALE('F');

    private final Character code;

    public static class Converter extends AbstractEnumAttributeConverter<Gender, Character> {
        public static final String ENUM_NAME = "성별";

        public Converter() {
            super(Gender.class, ENUM_NAME);
        }
    }
}
