package ifive.idrop.domain.subscription.dto;

import ifive.idrop.common.enums.Day;
import ifive.idrop.domain.pickup.PickUpSchedule;
import ifive.idrop.domain.subscription.Subscription;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class SubscriptionResponse {
    private Long subscriptionId;
    private LocalDateTime requestDate;
    private LocalDateTime responseDate;
    private LocalDateTime expiredDate;
    private String status;
    private String startAddress;
    private String endAddress;
    private Map<Day, String> schedule;

    public static SubscriptionResponse from(Subscription subscription) {
        Map<Day, String> schedule = new HashMap<>();
        List<PickUpSchedule> pickUpScheduleList = subscription.getPickUpScheduleList();
        for (PickUpSchedule pickUpSchedule : pickUpScheduleList) {
            LocalTime startTime = pickUpSchedule.getStartTime();
            String formattedTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            schedule.put(pickUpSchedule.getId().getDay(), formattedTime);
        }

        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getId())
                .requestDate(subscription.getRequestDate())
                .responseDate(subscription.getResponseDate())
                .expiredDate(subscription.getExpiredDate())
                .status(subscription.getStatus().getDesc())
                .startAddress(subscription.getPickUpLocation().getStartAddress())
                .endAddress(subscription.getPickUpLocation().getEndAddress())
                .schedule(schedule)
                .build();
    }
}
