package ifive.idrop.common.enums;

import jakarta.persistence.AttributeConverter;

public class GenderConverter implements AttributeConverter<Gender, Character> {
    @Override
    public Character convertToDatabaseColumn(Gender attribute) {
        if (attribute == Gender.M) {
            return 'M';
        }
        return 'F';
    }

    @Override
    public Gender convertToEntityAttribute(Character dbData) {
        if (dbData == 'M') {
            return Gender.M;
        }
        return Gender.F;
    }
}
