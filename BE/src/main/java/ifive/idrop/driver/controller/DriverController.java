package ifive.idrop.driver.controller;

import ifive.idrop.auth.resolver.Login;

import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.common.exception.CommonException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.dto.*;
import ifive.idrop.dto.CurrentPickUpResponse;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.service.DriverService;
import ifive.idrop.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;


@RequiredArgsConstructor
@RestController
@RequestMapping("/driver")
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/register/info")
    public BaseResponse<String> registerInfo(@Login Driver driver, @RequestBody DriverInformation driverInformation) {
        return driverService.registerInfo(driver.getId(), driverInformation);
    }

    @GetMapping("/pickup/now")
    public BaseResponse<List<CurrentPickUpResponse>> checkAllPickUpInfo(@Login Driver driver) {
        return driverService.getAllChildRunningInfo(driver);
    }

    @GetMapping("/pickup/now/child")
    public BaseResponse<List<CurrentPickUpResponse>> checkPickUpInfo(@Login Driver driver) {
        return driverService.getChildRunningInfo(driver);
    }

    @GetMapping("/subscribe/list")
    public List<DriverSubscribeInfoResponse> subscribeList(@Login Driver driver) {
        return driverService.subscribeList(driver.getId());
    }

    @PostMapping("/subscribe/check")
    public BaseResponse subscribeCheck(@Login Driver driver, @RequestBody SubscribeCheckRequest subscribeCheckRequest) throws ExecutionException, InterruptedException {
        return driverService.subscribeCheck(driver.getId(), subscribeCheckRequest);
    }

    @GetMapping("/pickup/today/remaining")
    public BaseResponse<List<DriverTodayRemainingPickUpResponse>> getRemainingPickUpList(@Login Driver driver) {
        List<DriverTodayRemainingPickUpResponse> pickUpList = driverService.getTodayRemainingPickUpList(driver.getId());
        return BaseResponse.of("Data Successfully Proceed", pickUpList);
    }

    @GetMapping("/detail/driver/{driverId}")
    public DriverDetailResponse detailDriver(@Login User user, @PathVariable("driverId") Long driverId) {
        if (user instanceof Driver driver) {
            if (!driver.getId().equals(driverId)) {
                throw new CommonException(ErrorCode.UNAUTHORIZED_USER);
            }
        }
        return driverService.detail(driverId);
    }
}
