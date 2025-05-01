package ifive.idrop.parent.service;


import ifive.idrop.child.domain.Child;
import ifive.idrop.common.enums.Day;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.parent.dto.SubscriptionRequest;
import ifive.idrop.dto.CurrentPickUpResponse;

import ifive.idrop.parent.dto.PickUpHistoryResponse;
import ifive.idrop.parent.dto.ParentSubscribeInfoResponse;
import ifive.idrop.pickup.domain.*;
import ifive.idrop.pickup.domain.enums.PickUpStatus;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.repository.DriverRepository;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.parent.repository.ParentRepository;
import ifive.idrop.pickup.repository.PickUpRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {
    private final DriverRepository driverRepository;
    private final ParentRepository parentRepository;
    private final PickUpRepository pickUpRepository;

    @Transactional
    public BaseResponse<String> subscribe(Parent parent, SubscriptionRequest subscriptionRequest) throws JSONException, ExecutionException, InterruptedException {

        Driver driver = driverRepository.findById(subscriptionRequest.getDriverId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DRIVER_NOT_EXIST));
        Child child = parentRepository.findChild(parent.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_EXIST));

        PickUpLocation location = createPickUpLocation(subscriptionRequest);
        createPickUpInfo(subscriptionRequest, child, driver, location);

        // 요청한 기사에게 알람
//        NotificationUtill.createNotification(driver, AlarmMessage.SUBSCRIBE_REQUEST.getTitle(),
//                AlarmMessage.SUBSCRIBE_REQUEST.getMessage());
        return BaseResponse.success();
    }

    public BaseResponse<List<CurrentPickUpResponse>> getChildRunningInfo(Parent parent) {
        List<Object[]> runningPickInfo = parentRepository.findRunningPickUpInfo(parent.getId());
        return BaseResponse.of("Data Successfully Proceed",
                runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((Subscription) o[0], (LocalDateTime) o[1]))
                        .toList());
    }

    private Subscription createPickUpInfo(SubscriptionRequest subscriptionRequest, Child child, Driver driver, PickUpLocation location) {
//        PickUpSubscription pickUpSubscription = PickUpSubscription.createPickUpSubscription();

        Subscription subscription = Subscription.builder()
                .child(child)
                .driver(driver)
                .status(PickUpStatus.WAIT)
                .requestDate(LocalDateTime.now())
                .pickUpScheduleList(new ArrayList<>())
                .build();
        subscription.updatePickUpLocation(location);
        pickUpRepository.savePickUpInfo(subscription);

        Map<Day, Map<String, Integer>> schedule = subscriptionRequest.getSchedule();
        for (Map.Entry<Day, Map<String, Integer>> entry : schedule.entrySet()) {
            Day day = entry.getKey();
            int hour = entry.getValue().get("hour");
            int min = entry.getValue().get("min");
            subscription.addPickUpSchedule(new PickUpSchedule(new PickUpScheduleId(subscription.getId(), day), LocalTime.of(hour, min), subscription));
        }
        return subscription;
    }

    private PickUpLocation createPickUpLocation(SubscriptionRequest subscriptionRequest) {
        PickUpLocation location = PickUpLocation.builder()
                .startAddress(subscriptionRequest.getStartAddress())
                .startLatitude(subscriptionRequest.getStartLatitude())
                .startLongitude(subscriptionRequest.getStartLongitude())
                .endAddress(subscriptionRequest.getEndAddress())
                .endLatitude(subscriptionRequest.getEndLatitude())
                .endLongitude(subscriptionRequest.getEndLongitude())
                .build();
        pickUpRepository.savePickUpLocation(location);
        return location;
    }

    public BaseResponse<List<PickUpHistoryResponse>> getPickUpHistoryInfo(Parent parent, long pickInfoId) {
        List<PickUpHistory> pickUpHistoryList = pickUpRepository.findPickUpByPickUpInfoIdAndParentIdOrderByReservedTime(parent.getId(), pickInfoId);
        return BaseResponse.of("Data Successfully Proceed",
                pickUpHistoryList.stream().map(PickUpHistoryResponse::toEntity)
                        .toList());
    }

    public List<ParentSubscribeInfoResponse> subscribeList(Long parentId) {
        List<Subscription> subscriptionList = pickUpRepository.findPickUpInfoByParentIdInTheLatestOrder(parentId);
        return subscriptionList.stream().map(ParentSubscribeInfoResponse::of).toList();
    }

    public boolean hasCurrentPickUp(Long parentId) {
        return pickUpRepository.getCurrentPickUpSize(parentId) != 0;
    }
}
