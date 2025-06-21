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
import java.util.StringTokenizer;

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

    @GetMapping
    public BaseResponse<List<DriverResponse>> searchDrivers(@RequestParam("start") String startLatLng, @RequestParam("goal") String goalLatLng) {
        StringTokenizer st = new StringTokenizer(startLatLng, ",");
        double startLat = Double.parseDouble(st.nextToken());
        double startLng = Double.parseDouble(st.nextToken());

        st = new StringTokenizer(goalLatLng, ",");
        double goalLat = Double.parseDouble(st.nextToken());
        double goalLng = Double.parseDouble(st.nextToken());

        List<DriverResponse> driverResponseList = driverService.searchDrivers(startLat, startLng, goalLat, goalLng)
                .stream()
                .map(DriverResponse::from)
                .toList();
        return BaseResponse.of("success", driverResponseList);
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
