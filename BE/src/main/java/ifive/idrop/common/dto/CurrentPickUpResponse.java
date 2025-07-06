package ifive.idrop.common.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import ifive.idrop.domain.subscription.entity.Subscription;
import ifive.idrop.domain.pickup.entity.PickUpLocation;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class CurrentPickUpResponse {
    private Long childId;
    private String childName;
    private String childImage;
    private LocalDateTime startDate;    // 구독 승인 날짜
    private LocalDateTime endDate;      // 구독 마감 날짜

    @JsonUnwrapped
    private Destination destination;

    @JsonUnwrapped
    private TimeInfo timeInfo;

    static public CurrentPickUpResponse of(Subscription subscription, LocalDateTime reservedTime) {
        return CurrentPickUpResponse.builder()
                .childId(subscription.getChild().getId())
                .childName(subscription.getChild().getName())
                .childImage(subscription.getChild().getImageUrl())
                .startDate(subscription.getResponseDate().plusDays(1))
                .endDate(subscription.getExpiredDate().minusDays(1))
                .destination(Destination.of(subscription.getPickUpLocation()))
                .timeInfo(TimeInfo.of(reservedTime))
                .build();
    }

    @Builder
    @AllArgsConstructor
    @Getter
    static class TimeInfo {
        private LocalDateTime pickUpStartTime;  // 실제 픽업 시작 시간
        private LocalDateTime pickUpEndTime;    // 실제 픽업 마감 시간

        static public TimeInfo of(LocalDateTime reservedTime) {
            return TimeInfo.builder()
                    .pickUpStartTime(reservedTime)
                    .pickUpEndTime(reservedTime.plusHours(1))
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @Getter
    static class Destination {
        private Double startLatitude;
        private Double startLongitude;
        private String startAddress;
        private Double endLatitude;
        private Double endLongitude;
        private String endAddress;

        static public Destination of(PickUpLocation location) {
            return Destination.builder()
                    .startAddress(location.getStartAddress())
                    .startLatitude(location.getStartLatitude())
                    .startLongitude(location.getStartLongitude())
                    .endAddress(location.getGoalAddress())
                    .endLatitude(location.getGoalLatitude())
                    .endLongitude(location.getGoalLongitude())
                    .build();
        }
    }
}
