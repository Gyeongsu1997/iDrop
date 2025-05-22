package ifive.idrop.pickup.domain.enums;

import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    //순서 수정하면 안됩니다.
    DECLINE("거절"), //기사가 요청을 거부한 상태
    ACCEPT("승인"), //기사가 요청을 수락한 상태
    WAIT("대기"), //부모가 요청을 보낸 상태
    CANCEL("취소"), //부모가 요청을 취소한 상태
    EXPIRED("만료"); //구독 기간이 끝나서 만료된 상태

    private static final SubscriptionStatus[] ENUMS = SubscriptionStatus.values();

    private final String status;

    SubscriptionStatus(String status) {
        this.status = status;
    }

    public static SubscriptionStatus of(int statusCode) {
        if (statusCode < 0 || statusCode > 4) {
            throw new BusinessException(ErrorCode.INVALID_PICKUP_STATUS);
        }
        return ENUMS[statusCode];
    }
}
