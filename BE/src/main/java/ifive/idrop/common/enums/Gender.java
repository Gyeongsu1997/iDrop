package ifive.idrop.common.enums;

import ifive.idrop.common.enums.converter.AbstractEnumAttributeConverter;
import ifive.idrop.common.enums.converter.BaseEnumAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender implements BaseEnumAttribute<Character> {
    MALE('M', "남성"),
    FEMALE('F', "여성");

    private final Character code;
    private final String desc;

    public static class Converter extends AbstractEnumAttributeConverter<Gender, Character> {
        public static final String ENUM_NAME = "성별";

        public Converter() {
            super(Gender.class, ENUM_NAME);
        }
    }
}
