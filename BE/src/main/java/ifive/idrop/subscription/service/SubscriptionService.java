package ifive.idrop.subscription.service;

import ifive.idrop.child.domain.Child;
import ifive.idrop.child.repository.ChildRepository;
import ifive.idrop.common.enums.Day;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.repository.DriverRepository;
import ifive.idrop.notification.AlarmMessage;
import ifive.idrop.notification.NotificationUtill;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.pickup.domain.PickUpHistory;
import ifive.idrop.pickup.domain.PickUpHistoryId;
import ifive.idrop.pickup.domain.PickUpSchedule;
import ifive.idrop.pickup.repository.PickUpRepository;
import ifive.idrop.subscription.domain.Subscription;
import ifive.idrop.subscription.dto.SubscriptionRequest;
import ifive.idrop.subscription.dto.SubscriptionResponse;
import ifive.idrop.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ifive.idrop.common.util.ScheduleUtils.isOverlapped;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {
    private final ChildRepository childRepository;
    private final DriverRepository driverRepository;
    private final PickUpRepository pickUpRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void subscribe(SubscriptionRequest subscriptionRequest) {
        Driver driver = driverRepository.findById(subscriptionRequest.getDriverId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DRIVER_NOT_EXIST));
        Child child = childRepository.findById(subscriptionRequest.getChildId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_EXIST));

        Subscription subscription = Subscription.createSubscription(subscriptionRequest, child, driver);
        subscriptionRepository.save(subscription);
    }

    public List<SubscriptionResponse> findDriverSubscriptions(Long driverId) {
        return subscriptionRepository.findByDriverId(driverId)
                .stream()
                .map(SubscriptionResponse::from)
                .toList();
    }

    @Transactional
    public void accept(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        subscription.accept();

        List<PickUpSchedule> pickUpScheduleList = subscription.getPickUpScheduleList();
        List<LocalDateTime> reservedTimeList = scheduleToReservedTime(pickUpScheduleList, subscription.getStartDate());
        short historySeq = 1;
        for (LocalDateTime reservedTime : reservedTimeList) {
            PickUpHistory pickUpHistory = PickUpHistory.createPickUpHistory(subscription, historySeq++, reservedTime);
            pickUpRepository.save(pickUpHistory);
        }
        // todo: 시간이 겹치는 요청 자동 거절
    }

    private List<LocalDateTime> scheduleToReservedTime(List<PickUpSchedule> pickUpScheduleList, LocalDate startDate) {
        Map<Day, LocalTime> scheduleMap = pickUpScheduleList.stream()
                .collect(Collectors.toMap(
                        PickUpSchedule::getDay,
                        PickUpSchedule::getStartTime
                ));

        List<LocalDateTime> reservedTimeList = new ArrayList<>();

        for (int i = 0; i < 28; i++) {
            LocalDate targetDate = startDate.plusDays(i);
            Day day = Day.fromDayOfWeek(targetDate.getDayOfWeek());

            if (scheduleMap.containsKey(day)) {
                LocalTime time = scheduleMap.get(day);
                reservedTimeList.add(LocalDateTime.of(targetDate, time));
            }
        }
        return reservedTimeList;
    }

    @Transactional
    public void reject(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        subscription.reject();
    }
}
