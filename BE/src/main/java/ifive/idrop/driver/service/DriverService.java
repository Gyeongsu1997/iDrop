package ifive.idrop.driver.service;

import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.domain.WorkSchedule;
import ifive.idrop.driver.dto.*;
import ifive.idrop.parent.dto.DriverListRequest;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.repository.DriverRepository;
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
            List<WorkSchedule> workScheduleList = driver.getWorkScheduleList();
            if (requestSchedule.isAvailable(workScheduleList, reservedSchedule)) {
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

    public BaseResponse<List<CurrentPickUpResponse>> getChildRunningInfo(Driver driver) {
        List<Object[]> runningPickInfo = driverRepository.findRunningPickUpInfo(driver.getId());
        return BaseResponse.of("Data Successfully Proceed",
                runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((Subscription) o[0], (LocalDateTime) o[1]))
                        .toList());
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
}