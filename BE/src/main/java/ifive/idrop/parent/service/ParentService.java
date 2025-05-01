package ifive.idrop.parent.service;


import ifive.idrop.child.domain.Child;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.parent.dto.SubscribeRequest;
import ifive.idrop.dto.CurrentPickUpResponse;

import ifive.idrop.parent.dto.PickUpHistoryResponse;
import ifive.idrop.parent.dto.ParentSubscribeInfoResponse;
import ifive.idrop.entity.*;
import ifive.idrop.entity.enums.PickUpStatus;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.notification.AlarmMessage;
import ifive.idrop.notification.NotificationUtill;
import ifive.idrop.driver.repository.DriverRepository;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.parent.repository.ParentRepository;
import ifive.idrop.pickup.domain.PickUp;
import ifive.idrop.pickup.repository.PickUpRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {
    private final DriverRepository driverRepository;
    private final ParentRepository parentRepository;
    private final PickUpRepository pickUpRepository;

    @Transactional
    public BaseResponse<String> createSubscribe(Parent parent, SubscribeRequest subscribeRequest) throws JSONException, ExecutionException, InterruptedException {
        Driver driver = driverRepository.findById(subscribeRequest.getDriverId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DRIVER_NOT_EXIST));
        Child child = parentRepository.findChild(parent.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_EXIST));

        PickUpLocation location = createPickUpLocation(subscribeRequest);
        createPickUpInfo(subscribeRequest, child, driver, location, subscribe);

        // 요청한 기사에게 알람
        NotificationUtill.createNotification(driver, AlarmMessage.SUBSCRIBE_REQUEST.getTitle(),
                AlarmMessage.SUBSCRIBE_REQUEST.getMessage());
        return BaseResponse.success();
    }

    public BaseResponse<List<CurrentPickUpResponse>> getChildRunningInfo(Parent parent) {
        List<Object[]> runningPickInfo = parentRepository.findRunningPickUpInfo(parent.getId());
        return BaseResponse.of("Data Successfully Proceed",
                runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((PickUpSubscription) o[0], (LocalDateTime) o[1]))
                        .toList());
    }

    private PickUpSubscription createPickUpInfo(SubscribeRequest subscribeRequest, Child child, Driver driver, PickUpLocation location) {
        PickUpSubscription pickUpSubscription = PickUpSubscription.builder()
                .child(child)
                .driver(driver)
                .schedule(subscribeRequest.getSchedule().toJSONString())
                .status(PickUpStatus.WAIT)
                .requestDate(LocalDateTime.now())
                .build();
        pickUpSubscription.updatePickUpLocation(location);
        pickUpRepository.savePickUpInfo(pickUpSubscription);
        return pickUpSubscription;
    }

    private PickUpLocation createPickUpLocation(SubscribeRequest subscribeRequest) {
        PickUpLocation location = PickUpLocation.builder()
                .startAddress(subscribeRequest.getStartAddress())
                .startLatitude(subscribeRequest.getStartLatitude())
                .startLongitude(subscribeRequest.getStartLongitude())
                .endAddress(subscribeRequest.getEndAddress())
                .endLatitude(subscribeRequest.getEndLatitude())
                .endLongitude(subscribeRequest.getEndLongitude())
                .build();
        pickUpRepository.savePickUpLocation(location);
        return location;
    }

    public BaseResponse<List<PickUpHistoryResponse>> getPickUpHistoryInfo(Parent parent, long pickInfoId) {
        List<PickUp> pickUpList = pickUpRepository.findPickUpByPickUpInfoIdAndParentIdOrderByReservedTime(parent.getId(), pickInfoId);
        return BaseResponse.of("Data Successfully Proceed",
                pickUpList.stream().map(PickUpHistoryResponse::toEntity)
                        .toList());
    }

    public List<ParentSubscribeInfoResponse> subscribeList(Long parentId) {
        List<PickUpSubscription> pickUpSubscriptionList = pickUpRepository.findPickUpInfoByParentIdInTheLatestOrder(parentId);
        return pickUpSubscriptionList.stream().map(ParentSubscribeInfoResponse::of).toList();
    }

    public boolean hasCurrentPickUp(Long parentId) {
        return pickUpRepository.getCurrentPickUpSize(parentId) != 0;
    }
}
