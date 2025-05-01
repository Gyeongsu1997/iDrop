package ifive.idrop.driver.dto;

import ifive.idrop.child.domain.Child;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.pickup.domain.PickUpLocation;
import ifive.idrop.pickup.domain.Subscription;
import lombok.Builder;
import lombok.Getter;
import org.json.simple.JSONObject;

import java.time.LocalDate;

import static ifive.idrop.common.util.ScheduleUtils.*;

@Builder
@Getter
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
    private JSONObject schedule;

    public static DriverSubscribeInfoResponse of(Subscription subscription) {
        Child child = subscription.getChild();
        Parent parent = child.getParent();
        PickUpLocation pickUpLocation = subscription.getPickUpLocation();

        LocalDate startDate = calculateStartDate(subscription.getModifiedDate());
        LocalDate endDate = calculateEndDate(subscription.getModifiedDate());

        return DriverSubscribeInfoResponse.builder()
                .pickUpInfoId(subscription.getId())
                .parentName(parent.getName())
                .parentPhoneNumber(parent.getPhoneNumber())
                .childName(child.getName())
                .childBirth(child.getBirthDate())
                .childGender(child.getGender().getLabel())
                .childImage(child.getImageUrl())
                .startDate(startDate)
                .endDate(endDate)
                .startAddress(pickUpLocation.getStartAddress())
                .endAddress(pickUpLocation.getEndAddress())
                .status(subscription.getStatus().getStatus())
//                .schedule(toJSONObject(pickUpSubscription.getSchedule()))
                .build();
    }
}
