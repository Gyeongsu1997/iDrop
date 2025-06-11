package ifive.idrop.domain.parent.dto;

import ifive.idrop.domain.driver.Driver;
import ifive.idrop.domain.subscription.Subscription;
import ifive.idrop.domain.pickup.PickUpLocation;
import lombok.Builder;
import lombok.Getter;
import org.json.simple.JSONObject;

import java.time.LocalDate;

import static ifive.idrop.common.util.ScheduleUtils.*;
import static ifive.idrop.common.util.ScheduleUtils.calculateEndDate;
import static ifive.idrop.common.util.ScheduleUtils.calculateStartDate;

@Builder
@Getter
public class ParentSubscribeInfoResponse {
    private Long pickUpInfoId;
    private String driverName;
    private String driverImage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startAddress; //출발지 주소
    private String endAddress; //목적지 주소
    private String status;
    private JSONObject schedule;

    public static ParentSubscribeInfoResponse of(Subscription subscription) {
        Driver driver = subscription.getDriver();
        PickUpLocation pickUpLocation = subscription.getPickUpLocation();

        LocalDate startDate = calculateStartDate(subscription.getResponseDate());
        LocalDate endDate = calculateEndDate(subscription.getResponseDate());

        return ParentSubscribeInfoResponse.builder()
                .pickUpInfoId(subscription.getId())
                .driverName(driver.getName())
                .driverImage(driver.getImageUrl())
                .startDate(startDate)
                .endDate(endDate)
                .startAddress(pickUpLocation.getStartAddress())
                .endAddress(pickUpLocation.getEndAddress())
                .status(subscription.getStatus().getDesc())
                .schedule(toJSONObject("pickUpSubscription.getSchedule()"))
                .build();
    }
}
