package ifive.idrop.common.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter(autoApply = true)
@RequiredArgsConstructor
public abstract class AbstractEnumAttributeConverter<E extends Enum<E> & BaseEnumAttribute<T>, T> implements AttributeConverter<E, T> {
    private final Class<E> enumClass;
    private final String enumName;
    private final boolean nullable;

    public AbstractEnumAttributeConverter(Class<E> enumClass, String enumName) {
        this.enumClass = enumClass;
        this.enumName = enumName;
        this.nullable = false;
    }

    @Override
    public T convertToDatabaseColumn(E attribute) {
        if (!nullable && attribute == null) {
            throw new IllegalArgumentException(String.format("%s(은)는 DB에 NULL로 저장할 수 없습니다.", enumName));
        }
        return EnumAttributeConvertUtils.toCode(attribute);
    }

    @Override
    public E convertToEntityAttribute(T dbData) {
        if (!nullable && dbData == null) {
            throw new IllegalArgumentException(String.format("%s(이)가 DB에 NULL로 저장되어 있습니다.", enumName));
        }
        return EnumAttributeConvertUtils.ofCode(enumClass, dbData);
    }
}
