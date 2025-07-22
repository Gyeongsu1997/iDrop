package ifive.idrop.domain.parent.controller;

import ifive.idrop.domain.driver.dto.CurrentPickUpResponse;
import ifive.idrop.domain.parent.dto.ParentSubscribeInfoResponse;
import ifive.idrop.domain.driver.entity.Driver;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.domain.driver.service.DriverService;
import ifive.idrop.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import ifive.idrop.domain.auth.resolver.Login;
import ifive.idrop.common.response.DataResponse;
import ifive.idrop.domain.parent.entity.Parent;
import ifive.idrop.domain.parent.service.ParentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parent")
public class ParentController {
    private final DriverService driverService;
    private final ParentService parentService;

    @GetMapping("/pickup/now")
    public DataResponse<List<CurrentPickUpResponse>> checkPickUpInfo(@Login Parent parent) {
        return parentService.getChildRunningInfo(parent);
    }

    @GetMapping("/history/{pickup-info-id}")
    public DataResponse checkHistoryInfo(@Login Parent parent, @PathVariable(value = "pickup-info-id") long pickInfoId) {
        return parentService.getPickUpHistoryInfo(parent, pickInfoId);
    }

    @GetMapping("/subscribe/list")
    public List<ParentSubscribeInfoResponse> subscribeList(@Login Parent parent) {
        return parentService.subscribeList(parent.getId());
    }

    @GetMapping("/location/now")
    public DataResponse hasCurrentPickUp(@Login Parent parent) {
        if(parentService.hasCurrentPickUp(parent.getId()))
            return DataResponse.success();
        throw new BusinessException(ErrorCode.PICKUP_NOT_FOUND);
    }

    @GetMapping("/user/pickup/now")
    public DataResponse checkPickUpInfo(@Login User user) {
        if (user instanceof Parent) {
            return parentService.getChildRunningInfo((Parent) user);
        }

        if (user instanceof Driver) {
            return driverService.getAllChildRunningInfo((Driver) user);
        }

        throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
    }
}
