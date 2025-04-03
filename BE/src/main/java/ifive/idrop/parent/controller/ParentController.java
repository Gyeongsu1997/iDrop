package ifive.idrop.parent.controller;

import ifive.idrop.dto.DriverListRequest;
import ifive.idrop.dto.CurrentPickUpResponse;
import ifive.idrop.parent.dto.DriverListResponse;
import ifive.idrop.parent.dto.ParentSubscribeInfoResponse;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.common.exception.CommonException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.service.DriverService;
import ifive.idrop.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ifive.idrop.auth.resolver.Login;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.parent.dto.SubscribeRequest;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.parent.service.ParentService;
import org.json.JSONException;


@RequiredArgsConstructor
@RestController
@RequestMapping("/parent")
public class ParentController {
    private final DriverService driverService;
    private final ParentService parentService;

    @PostMapping("/search/drivers")
    public DriverListResponse searchDrivers(@RequestBody DriverListRequest driverListRequest) {
        DriverListResponse driverListResponse = new DriverListResponse();

        List<Driver> drivers = driverService.searchAvailableDrivers(driverListRequest);

        for (Driver driver : drivers) {
            driverListResponse.addDriverSummary(driver.getSummary());
        }
        return driverListResponse;
    }

    @PostMapping("/subscribe")
    public BaseResponse<String> subscribeDriver(@Login Parent parent, @RequestBody SubscribeRequest request) throws JSONException, ExecutionException, InterruptedException {
        return parentService.createSubscribe(parent, request);
    }

    @GetMapping("/pickup/now")
    public BaseResponse<List<CurrentPickUpResponse>> checkPickUpInfo(@Login Parent parent) {
        return parentService.getChildRunningInfo(parent);
    }

    @GetMapping("/history/{pickup-info-id}")
    public BaseResponse checkHistoryInfo(@Login Parent parent, @PathVariable(value = "pickup-info-id") long pickInfoId) {
        return parentService.getPickUpHistoryInfo(parent, pickInfoId);
    }

    @GetMapping("/subscribe/list")
    public List<ParentSubscribeInfoResponse> subscribeList(@Login Parent parent) {
        return parentService.subscribeList(parent.getId());
    }

    @GetMapping("/location/now")
    public BaseResponse hasCurrentPickUp(@Login Parent parent) {
        if(parentService.hasCurrentPickUp(parent.getId()))
            return BaseResponse.success();
        throw new CommonException(ErrorCode.PICKUP_NOT_FOUND);
    }

    @GetMapping("/user/pickup/now")
    public BaseResponse checkPickUpInfo(@Login User user) {
        if (user instanceof Parent) {
            return parentService.getChildRunningInfo((Parent) user);
        }

        if (user instanceof Driver) {
            return driverService.getAllChildRunningInfo((Driver) user);
        }

        throw new CommonException(ErrorCode.UNAUTHORIZED_USER);
    }
}
