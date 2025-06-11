package ifive.idrop.domain.driver.controller;

import ifive.idrop.domain.auth.resolver.Login;

import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.domain.driver.dto.DriverInformation;
import ifive.idrop.domain.driver.dto.DriverResponse;
import ifive.idrop.domain.driver.dto.DriverTodayRemainingPickUpResponse;
import ifive.idrop.common.dto.CurrentPickUpResponse;
import ifive.idrop.domain.driver.Driver;
import ifive.idrop.domain.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverService driverService;

    @GetMapping("/{driverId}")
    public BaseResponse<DriverResponse> getDriver(@PathVariable Long driverId) {
        Driver driver = driverService.findDriver(driverId);
        return BaseResponse.of("success", DriverResponse.from(driver));
    }

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

    @GetMapping("/pickup/today/remaining")
    public BaseResponse<List<DriverTodayRemainingPickUpResponse>> getRemainingPickUpList(@Login Driver driver) {
        List<DriverTodayRemainingPickUpResponse> pickUpList = driverService.getTodayRemainingPickUpList(driver.getId());
        return BaseResponse.of("Data Successfully Proceed", pickUpList);
    }
}
