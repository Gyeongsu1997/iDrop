package ifive.idrop.common.enums.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumAttributeConvertUtils {
    public static <E extends Enum<E> & BaseEnumAttribute<T>, T> T toCode(E enumAttribute) {
        if (enumAttribute == null) {
            return null;
        }
        return enumAttribute.getCode();
    }

    public static <E extends Enum<E> & BaseEnumAttribute<T>, T> E ofCode(Class<E> enumClass, T code) {
        if (code == null) {
            return null;
        }
        return EnumSet.allOf(enumClass).stream()
                .filter(e -> e.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown database value [%s] for enum [%s]", code, enumClass.getName())));
    }
}
