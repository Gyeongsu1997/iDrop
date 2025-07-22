package ifive.idrop.domain.subscription.dto;

import ifive.idrop.domain.child.entity.Child;
import ifive.idrop.domain.parent.entity.Parent;
import ifive.idrop.domain.pickup.entity.PickUpLocation;
import ifive.idrop.domain.subscription.entity.Subscription;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

import static ifive.idrop.common.util.ScheduleUtils.calculateEndDate;
import static ifive.idrop.common.util.ScheduleUtils.calculateStartDate;

@Getter
@Builder
public class DriverSubscribeInfoResponse {
    private Long pickUpInfoId;
    private String parentName;
    private String parentPhoneNumber;
    private String childName;
    private LocalDate childBirth;
    private String childGender;
    private String childImage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startAddress; //출발지 주소
    private String endAddress; //목적지 주소
    private String status;

    public static DriverSubscribeInfoResponse of(Subscription subscription) {
        Child child = subscription.getChild();
        Parent parent = child.getParent();
        PickUpLocation pickUpLocation = subscription.getPickUpLocation();

        LocalDate startDate = calculateStartDate(subscription.getResponseDate());
        LocalDate endDate = calculateEndDate(subscription.getResponseDate());

        return DriverSubscribeInfoResponse.builder()
                .pickUpInfoId(subscription.getId())
                .parentName(parent.getName())
                .parentPhoneNumber(parent.getPhoneNumber())
                .childName(child.getName())
                .childBirth(child.getBirthDate())
//                .childGender(child.getGender().getLabel())
                .childImage(child.getImageUrl())
                .startDate(startDate)
                .endDate(endDate)
                .startAddress(pickUpLocation.getStartAddress())
                .endAddress(pickUpLocation.getGoalAddress())
                .status(subscription.getStatus().getDesc())
//                .schedule(toJSONObject(pickUpSubscription.getSchedule()))
                .build();
    }
}
