package ifive.idrop.domain.driver.controller;

import ifive.idrop.domain.auth.resolver.Login;

import ifive.idrop.common.response.DataResponse;
import ifive.idrop.domain.driver.dto.DriverInformation;
import ifive.idrop.domain.driver.dto.DriverResponse;
import ifive.idrop.domain.driver.dto.DriverTodayRemainingPickUpResponse;
import ifive.idrop.domain.driver.dto.CurrentPickUpResponse;
import ifive.idrop.domain.driver.entity.Driver;
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
    public DataResponse<DriverResponse> getDriver(@PathVariable Long driverId) {
        DriverResponse driverResponse = driverService.findDriver(driverId);
        return DataResponse.of(driverResponse);
    }

    @GetMapping
    public DataResponse<List<DriverResponse>> searchDrivers(@RequestParam("start") String startLatLng, @RequestParam("goal") String goalLatLng) {
        StringTokenizer st = new StringTokenizer(startLatLng, ",");
        double startLat = Double.parseDouble(st.nextToken());
        double startLng = Double.parseDouble(st.nextToken());

        st = new StringTokenizer(goalLatLng, ",");
        double goalLat = Double.parseDouble(st.nextToken());
        double goalLng = Double.parseDouble(st.nextToken());

        List<DriverResponse> driverResponseList = driverService.searchDrivers(startLat, startLng, goalLat, goalLng);
        return DataResponse.of(driverResponseList);
    }

    @PostMapping("/register/info")
    public DataResponse<?> registerInfo(@Login Driver driver, @RequestBody DriverInformation driverInformation) {
        return driverService.registerInfo(driver.getId(), driverInformation);
    }

    @GetMapping("/pickup/now")
    public DataResponse<List<CurrentPickUpResponse>> checkAllPickUpInfo(@Login Driver driver) {
        return driverService.getAllChildRunningInfo(driver);
    }

    @GetMapping("/pickup/now/child")
    public DataResponse<List<CurrentPickUpResponse>> checkPickUpInfo(@Login Driver driver) {
        return driverService.getChildRunningInfo(driver);
    }

    @GetMapping("/pickup/today/remaining")
    public DataResponse<List<DriverTodayRemainingPickUpResponse>> getRemainingPickUpList(@Login Driver driver) {
        List<DriverTodayRemainingPickUpResponse> pickUpList = driverService.getTodayRemainingPickUpList(driver.getId());
        return DataResponse.of(pickUpList);
    }
}
