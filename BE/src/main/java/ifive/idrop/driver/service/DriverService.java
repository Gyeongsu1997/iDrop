package ifive.idrop.driver.service;

import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.domain.WorkHours;
import ifive.idrop.driver.dto.*;
import ifive.idrop.parent.dto.DriverListRequest;
import ifive.idrop.subscription.domain.SubscriptionStatus;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.notification.AlarmMessage;
import ifive.idrop.notification.NotificationUtill;
import ifive.idrop.driver.repository.DriverRepository;
import ifive.idrop.notification.domain.Notification;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.pickup.domain.PickUpHistory;
import ifive.idrop.notification.repository.NotificationRepository;
import ifive.idrop.pickup.repository.PickUpRepository;
import ifive.idrop.common.util.RequestSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import ifive.idrop.common.dto.CurrentPickUpResponse;
import ifive.idrop.subscription.domain.Subscription;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ifive.idrop.common.util.ScheduleUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {
    private final DriverRepository driverRepository;
    private final PickUpRepository pickUpRepository;
    private final NotificationRepository notificationRepository;

    public Driver findDriver(Long driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public List<Driver> searchAvailableDrivers(DriverListRequest driverListRequest) {
        RequestSchedule requestSchedule = parseToList(driverListRequest.getSchedule());

        List<Driver> availableDrivers = new ArrayList<>();
        List<Driver> drivers = driverRepository.findAllDrivers();
        for (Driver driver : drivers) {
            List<PickUpHistory> pickUpHistoryList = pickUpRepository.findReservedPickUpsByDriver(driver.getId());
            List<LocalDateTime> reservedSchedule = pickUpHistoryList.stream()
                    .map(PickUpHistory::getReservedTime)
                    .toList();
            List<WorkHours> workHoursList = driver.getWorkHoursList();
            if (requestSchedule.isAvailable(workHoursList, reservedSchedule)) {
                availableDrivers.add(driver);
            }
        }
        return availableDrivers;
    }

    @Transactional
    public BaseResponse<String> registerInfo(Long driverId, DriverInformation driverInformation) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        driver.addAdditionalInfo(driverInformation);
        return BaseResponse.of("정보가 성공적으로 등록되었습니다.", driver.getName());
    }

    public BaseResponse<List<CurrentPickUpResponse>> getAllChildRunningInfo(Driver driver) {
        List<Object[]> runningPickInfo = driverRepository.findAllRunningPickUpInfoOrderByreservedTimeASC(driver.getId());
        return BaseResponse.of("Data Successfully Proceed",
                runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((Subscription) o[0], (LocalDateTime) o[1]))
                        .toList());
    }

    public List<DriverSubscribeInfoResponse> subscribeList(Long driverId) {
        List<Subscription> subscriptionList = pickUpRepository.findPickUpInfoByDriverIdTheLatestOrder(driverId);
        return subscriptionList.stream().map(DriverSubscribeInfoResponse::of).toList();
    }

    public BaseResponse<List<CurrentPickUpResponse>> getChildRunningInfo(Driver driver) {
        List<Object[]> runningPickInfo = driverRepository.findRunningPickUpInfo(driver.getId());
        return BaseResponse.of("Data Successfully Proceed",
                runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((Subscription) o[0], (LocalDateTime) o[1]))
                        .toList());
    }

    @Transactional
    public BaseResponse subscribeCheck(Long driverId, SubscribeCheckRequest subscribeCheckRequest) {
        Integer statusCode = subscribeCheckRequest.getStatusCode();
        if (statusCode == null || !(statusCode == 0 || statusCode == 1)) { // 0: 거절 / 1: 승인
            throw new BusinessException(ErrorCode.INVALID_PICKUP_STATUS);
        }
        Long pickUpInfoId = subscribeCheckRequest.getPickUpInfoId();
        Subscription subscription = pickUpRepository.findPickUpInfoById(pickUpInfoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_INFO_NOT_EXIST));

        if (!Objects.equals(driverId, subscription.getDriver().getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }

//        SubscriptionStatus subscriptionStatus = subscription.modify(SubscriptionStatus.of(statusCode));
        SubscriptionStatus subscriptionStatus = SubscriptionStatus.PROGRESS;


        if (subscriptionStatus.equals(SubscriptionStatus.PROGRESS)) {
            RequestSchedule requestSchedule = parseToList(subscription.getPickUpScheduleList());
            List<LocalDateTime> requestScheduleList = requestSchedule.getRequestSchedule();
            for (LocalDateTime reservedTime : requestScheduleList) {
                createPickUp(reservedTime, subscription);
            }
            removeOverlappedSubscribe(driverId, subscription); // 승인한 구독 요청과 시간이 겹치는 다른 구독 요청을 거절로 처리함

            // 알림 보내기
//            Parent parent = pickUpSubscription.getChild().getParent();
//            NotificationUtill.createNotification(parent,
//                    AlarmMessage.APPROVE.getTitle(), AlarmMessage.APPROVE.getMessage());

            return BaseResponse.from("요청을 성공적으로 승인했습니다.");
        } else {
            Parent parent = subscription.getChild().getParent();
            NotificationUtill.createNotification(parent,
                    AlarmMessage.DECLINE.getTitle(), AlarmMessage.DECLINE.getMessage());

            return BaseResponse.from("요청을 성공적으로 거절했습니다.");
        }
        //TODO Alarm to Parent
    }

    public List<DriverTodayRemainingPickUpResponse> getTodayRemainingPickUpList(Long driverId) {
        List<Object[]> remainingPickUpInfo = driverRepository.findRemainingPickUpInfo(driverId);

        return remainingPickUpInfo.stream()
                .map(o -> {
                    Subscription po = (Subscription) o[0];
                    LocalDateTime reservedTime = (LocalDateTime) o[1];
                    return DriverTodayRemainingPickUpResponse.of(po, reservedTime);
                })
                .collect(Collectors.toList());
    }


    private void createPickUp(LocalDateTime localDateTime, Subscription subscription) {
        PickUpHistory pickUpHistory = PickUpHistory.builder()
                .reservedTime(localDateTime)
                .build();
        pickUpHistory.updatePickUpInfo(subscription);
        pickUpRepository.savePickUp(pickUpHistory);

        // Notifiaciton 생성
        Notification notification = Notification.builder()
                .driver(subscription.getDriver())
                .pickUpAlarmTime(localDateTime.minusHours(1))
                .build();
        notificationRepository.save(notification);
    }

    private void removeOverlappedSubscribe(Long driverId, Subscription subscription) {
        List<Subscription> waitingSubscriptionList = pickUpRepository.findWaitingPickUpInfoByDriverId(driverId);
        for (Subscription waitingSubscription : waitingSubscriptionList) {
            if (isOverlapped("pickUpSubscription.getSchedule()", "waitingPickUpSubscription.getSchedule()")) {
                waitingSubscription.modify(SubscriptionStatus.REJECTED);

                // 거절 알람
                Parent parent = subscription.getParent();
                NotificationUtill.createNotification(parent, AlarmMessage.DECLINE.getTitle(),
                        AlarmMessage.DECLINE.getMessage());
            }
        }
    }
}