package ifive.idrop.parent.dto;

import ifive.idrop.driver.domain.Driver;
import ifive.idrop.entity.PickUpSubscription;
import ifive.idrop.entity.PickUpLocation;
import lombok.Builder;
import lombok.Getter;
import org.json.simple.JSONObject;

import java.time.LocalDate;

import static ifive.idrop.util.ScheduleUtils.*;
import static ifive.idrop.util.ScheduleUtils.calculateEndDate;
import static ifive.idrop.util.ScheduleUtils.calculateStartDate;

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

    public static ParentSubscribeInfoResponse of(PickUpSubscription pickUpSubscription) {
        Driver driver = pickUpSubscription.getDriver();
        PickUpLocation pickUpLocation = pickUpSubscription.getPickUpLocation();

        LocalDate startDate = calculateStartDate(pickUpSubscription.getModifiedDate());
        LocalDate endDate = calculateEndDate(pickUpSubscription.getModifiedDate());

        return ParentSubscribeInfoResponse.builder()
                .pickUpInfoId(pickUpSubscription.getId())
                .driverName(driver.getName())
                .driverImage(driver.getImageUrl())
                .startDate(startDate)
                .endDate(endDate)
                .startAddress(pickUpLocation.getStartAddress())
                .endAddress(pickUpLocation.getEndAddress())
                .status(pickUpSubscription.getStatus().getStatus())
                .schedule(toJSONObject(pickUpSubscription.getSchedule()))
                .build();
    }
}
