package ifive.idrop.domain.driver.service;

import ifive.idrop.common.dto.DataResponse;
import ifive.idrop.domain.driver.dto.DriverResponse;
import ifive.idrop.domain.driver.entity.Driver;
import ifive.idrop.domain.driver.dto.DriverInformation;
import ifive.idrop.domain.driver.dto.DriverTodayRemainingPickUpResponse;
import ifive.idrop.domain.driver.repository.DriverRepository;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import ifive.idrop.common.dto.CurrentPickUpResponse;
import ifive.idrop.domain.subscription.entity.Subscription;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverResponse findDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return DriverResponse.from(driver);
    }

    public List<DriverResponse> searchDrivers(double startLat, double startLng, double goalLat, double goalLng) {
        return driverRepository.findByLocation(startLat, startLng, goalLat, goalLng)
                .stream()
                .map(DriverResponse::from)
                .toList();
    }

    @Transactional
    public DataResponse<?> registerInfo(Long driverId, DriverInformation driverInformation) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        driver.addAdditionalInfo(driverInformation);
        return DataResponse.success();
    }

    public DataResponse<List<CurrentPickUpResponse>> getAllChildRunningInfo(Driver driver) {
        List<Object[]> runningPickInfo = driverRepository.findAllRunningPickUpInfoOrderByreservedTimeASC(driver.getId());
        return DataResponse.of(runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((Subscription) o[0], (LocalDateTime) o[1]))
                        .toList());
    }

    public DataResponse<List<CurrentPickUpResponse>> getChildRunningInfo(Driver driver) {
        List<Object[]> runningPickInfo = driverRepository.findRunningPickUpInfo(driver.getId());
        return DataResponse.of(runningPickInfo.stream()
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