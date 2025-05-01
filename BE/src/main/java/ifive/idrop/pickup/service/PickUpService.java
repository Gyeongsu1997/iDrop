package ifive.idrop.pickup.service;

import ifive.idrop.child.domain.Child;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.common.enums.Day;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.repository.DriverRepository;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.parent.dto.SubscriptionRequest;
import ifive.idrop.parent.repository.ParentRepository;
import ifive.idrop.pickup.domain.*;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.notification.AlarmMessage;
import ifive.idrop.notification.NotificationUtill;
import ifive.idrop.pickup.domain.enums.PickUpStatus;
import ifive.idrop.pickup.repository.PickUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PickUpService {
    private final PickUpRepository pickUpRepository;
    private final DriverRepository driverRepository;
    private final ParentRepository parentRepository;

    private final ImageService imageService;
    private final String PICKUP_IMAGE_PATH = "image/pickup/";

    @Transactional
    public BaseResponse<String> subscribe(Parent parent, SubscriptionRequest subscriptionRequest) {
        Driver driver = driverRepository.findById(subscriptionRequest.getDriverId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DRIVER_NOT_EXIST));
        Child child = parentRepository.findChild(parent.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_EXIST));

        PickUpLocation pickUpLocation = PickUpLocation.createPickUpLocation(subscriptionRequest);
        createPickUpInfo(subscriptionRequest, child, driver, pickUpLocation);

        // 요청한 기사에게 알람
//        NotificationUtill.createNotification(driver, AlarmMessage.SUBSCRIBE_REQUEST.getTitle(),
//                AlarmMessage.SUBSCRIBE_REQUEST.getMessage());
        return BaseResponse.success();
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

    @Transactional
    public void saveStartOrEndPickUp(Long pickUpId, MultipartFile image, String message) throws IOException, ExecutionException, InterruptedException {
        PickUpHistory pickUpHistory = pickUpRepository.findPickUpById(pickUpId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_NOT_FOUND));
        Parent parent = pickUpHistory.getParent();
        if (pickUpHistory.getStartImage() == null) {
            String imageUrl = imageService.upload(image, PICKUP_IMAGE_PATH);
            pickUpRepository.savePickUpStartInfo(pickUpId, imageUrl, message);
            log.info("pickUp Start - driverId = {}, pickUpId = {}", pickUpHistory.getDriver().getId(), pickUpHistory.getId());

            NotificationUtill.createNotification(parent, AlarmMessage.PICK_UP_START.getTitle(),
                    AlarmMessage.PICK_UP_START.getMessage());
        } else if (pickUpHistory.getEndImage() == null) {
            String imageUrl = imageService.upload(image, PICKUP_IMAGE_PATH);
            pickUpRepository.savePickUpEndInfo(pickUpId, imageUrl, message);
            log.info("pickUp End - driverId = {}, pickUpId = {}", pickUpHistory.getDriver().getId(), pickUpHistory.getId());

            NotificationUtill.createNotification(parent, AlarmMessage.PICK_UP_END.getTitle(),
                    AlarmMessage.PICK_UP_END.getMessage());
        } else {
            throw new BusinessException(ErrorCode.PICKUP_ALREADY_END);
        }
    }

    public Optional<PickUpHistory> findCurrentPickUp(Long driverId, Long childId) {
        List<PickUpHistory> pickUpHistories = pickUpRepository.findPickUpsByDriverIdWithCurrentTimeInReservedRange(driverId);
        Optional<PickUpHistory> result = pickUpHistories.stream()
                .filter(p -> p.getChild().getId().equals(childId))
                .findFirst();
        return result;
    }
}
