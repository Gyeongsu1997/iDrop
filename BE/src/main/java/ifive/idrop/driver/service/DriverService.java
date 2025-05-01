package ifive.idrop.driver.service;

import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.domain.WorkHours;
import ifive.idrop.driver.dto.*;
import ifive.idrop.parent.dto.DriverListRequest;
import ifive.idrop.entity.*;
import ifive.idrop.entity.enums.PickUpStatus;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.notification.AlarmMessage;
import ifive.idrop.notification.NotificationUtill;
import ifive.idrop.driver.repository.DriverRepository;
import ifive.idrop.notification.domain.Notification;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.pickup.domain.PickUp;
import ifive.idrop.notification.repository.NotificationRepository;
import ifive.idrop.pickup.repository.PickUpRepository;
import ifive.idrop.util.RequestSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import ifive.idrop.dto.CurrentPickUpResponse;
import ifive.idrop.entity.PickUpSubscription;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static ifive.idrop.util.ScheduleUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {
    private final DriverRepository driverRepository;
    private final PickUpRepository pickUpRepository;
    private final NotificationRepository notificationRepository;

    public List<Driver> searchAvailableDrivers(DriverListRequest driverListRequest) {
        RequestSchedule requestSchedule = parseToList(driverListRequest.getSchedule());

        List<Driver> availableDrivers = new ArrayList<>();
        List<Driver> drivers = driverRepository.findAllDrivers();
        for (Driver driver : drivers) {
            List<PickUp> pickUpList = pickUpRepository.findReservedPickUpsByDriver(driver.getId());
            List<LocalDateTime> reservedSchedule = pickUpList.stream()
                    .map(PickUp::getReservedTime)
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

    public DriverDetailResponse detail(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return driver.getDetail();
    }


    public BaseResponse<List<CurrentPickUpResponse>> getAllChildRunningInfo(Driver driver) {
        List<Object[]> runningPickInfo = driverRepository.findAllRunningPickUpInfoOrderByreservedTimeASC(driver.getId());
        return BaseResponse.of("Data Successfully Proceed",
                runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((PickUpSubscription) o[0], (LocalDateTime) o[1]))
                        .toList());
    }

    public List<DriverSubscribeInfoResponse> subscribeList(Long driverId) {
        List<PickUpSubscription> pickUpSubscriptionList = pickUpRepository.findPickUpInfoByDriverIdTheLatestOrder(driverId);
        return pickUpSubscriptionList.stream().map(DriverSubscribeInfoResponse::of).toList();
    }

    public BaseResponse<List<CurrentPickUpResponse>> getChildRunningInfo(Driver driver) {
        List<Object[]> runningPickInfo = driverRepository.findRunningPickUpInfo(driver.getId());
        return BaseResponse.of("Data Successfully Proceed",
                runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((PickUpSubscription) o[0], (LocalDateTime) o[1]))
                        .toList());
    }

    @Transactional
    public BaseResponse subscribeCheck(Long driverId, SubscribeCheckRequest subscribeCheckRequest) throws ExecutionException, InterruptedException {
        Integer statusCode = subscribeCheckRequest.getStatusCode();
        if (statusCode == null || !(statusCode == 0 || statusCode == 1)) {
            throw new BusinessException(ErrorCode.INVALID_PICKUP_STATUS);
        }
        Long pickUpInfoId = subscribeCheckRequest.getPickUpInfoId();
        PickUpSubscription pickUpSubscription = pickUpRepository.findPickUpInfoById(pickUpInfoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_INFO_NOT_EXIST));

        if (!Objects.equals(driverId, pickUpSubscription.getDriver().getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }

        PickUpStatus pickUpStatus =  pickUpSubscription.modify(PickUpStatus.of(statusCode));

        if (pickUpStatus.equals(PickUpStatus.ACCEPT)) {
//            RequestSchedule requestSchedule = parseToList(toJSONObject(pickUpSubscription.getSchedule()));
            RequestSchedule requestSchedule = parseToList(toJSONObject("pickUpSubscription.getSchedule()"));
            List<LocalDateTime> requestScheduleList = requestSchedule.getRequestSchedule();
            for (LocalDateTime reservedTime : requestScheduleList) {
                createPickUp(reservedTime, pickUpSubscription);
            }
            removeOverlappedSubscribe(driverId, pickUpSubscription); //승인한 구독 요청과 시간이 겹치는 다른 구독 요청을 거절로 처리함

            // 알림 보내기
            Parent parent = pickUpSubscription.getChild().getParent();
            NotificationUtill.createNotification(parent,
                    AlarmMessage.APPROVE.getTitle(), AlarmMessage.APPROVE.getMessage());

            return BaseResponse.from("요청을 성공적으로 승인했습니다.");
        } else {
            Parent parent = pickUpSubscription.getChild().getParent();
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
                    PickUpSubscription po = (PickUpSubscription) o[0];
                    LocalDateTime reservedTime = (LocalDateTime) o[1];
                    return DriverTodayRemainingPickUpResponse.of(po, reservedTime);
                })
                .collect(Collectors.toList());
    }


    private void createPickUp(LocalDateTime localDateTime, PickUpSubscription pickUpSubscription) {
        PickUp pickUp = PickUp.builder()
                .reservedTime(localDateTime)
                .build();
        pickUp.updatePickUpInfo(pickUpSubscription);
        pickUpRepository.savePickUp(pickUp);

        // Notifiaciton 생성
        Notification notification = Notification.builder()
                .driver(pickUpSubscription.getDriver())
                .pickUpAlarmTime(localDateTime.minusHours(1))
                .build();
        notificationRepository.save(notification);
    }

    private void removeOverlappedSubscribe(Long driverId, PickUpSubscription pickUpSubscription) {
        List<PickUpSubscription> waitingPickUpSubscriptionList = pickUpRepository.findWaitingPickUpInfoByDriverId(driverId);
        for (PickUpSubscription waitingPickUpSubscription : waitingPickUpSubscriptionList) {
            if (isOverlapped("pickUpSubscription.getSchedule()", "waitingPickUpSubscription.getSchedule()")) {
                waitingPickUpSubscription.modify(PickUpStatus.DECLINE);

                // 거절 알람
                Parent parent = pickUpSubscription.getParent();
                NotificationUtill.createNotification(parent, AlarmMessage.DECLINE.getTitle(),
                        AlarmMessage.DECLINE.getMessage());
            }
        }
    }
}