package ifive.idrop.domain.parent.dto;

import ifive.idrop.domain.driver.entity.Driver;
import ifive.idrop.domain.subscription.entity.Subscription;
import ifive.idrop.domain.pickup.entity.PickUpLocation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ParentSubscribeInfoResponse {
    private Long pickUpInfoId;
    private String driverName;
    private String driverImage;
    private LocalDate startDate;
    private String startAddress; //출발지 주소
    private String endAddress; //목적지 주소
    private String status;

    public static ParentSubscribeInfoResponse of(Subscription subscription) {
        Driver driver = subscription.getDriver();
        PickUpLocation pickUpLocation = subscription.getPickUpLocation();

        return ParentSubscribeInfoResponse.builder()
                .pickUpInfoId(subscription.getId())
                .driverName(driver.getName())
                .driverImage(driver.getImageUrl())
                .startDate(subscription.getStartDate())
                .startAddress(pickUpLocation.getStartAddress())
                .endAddress(pickUpLocation.getGoalAddress())
                .status(subscription.getStatus().getDesc())
                .build();
    }
}
