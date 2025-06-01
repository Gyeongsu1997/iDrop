package ifive.idrop.subscription.domain;

import ifive.idrop.common.enums.converter.AbstractEnumAttributeConverter;
import ifive.idrop.common.enums.converter.BaseEnumAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus implements BaseEnumAttribute<Integer> {
    REQUEST(1, "요청"),
    CANCELED(2, "취소"),
    PROGRESS(3, "진행중"),
    REJECTED(4, "거부"),
    EXPIRED(5, "만료");

    private final Integer code;
    private final String desc;

    public static class Converter extends AbstractEnumAttributeConverter<SubscriptionStatus, Integer> {
        public static final String ENUM_NAME = "구독 상태";

        public Converter() {
            super(SubscriptionStatus.class, ENUM_NAME);
        }
    }
}
