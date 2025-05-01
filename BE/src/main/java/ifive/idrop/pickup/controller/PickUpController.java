package ifive.idrop.pickup.controller;

import ifive.idrop.auth.resolver.Login;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.pickup.domain.PickUpHistory;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.pickup.service.PickUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PickUpController {

    private final PickUpService pickUpService;

    @PostMapping("/driver/pickup")
    public BaseResponse<String> startOrEndPickUp(@Login Driver driver, Long childId, @ModelAttribute MultipartFile image, String message) throws ExecutionException, InterruptedException {
        PickUpHistory pickUpHistory = pickUpService.findCurrentPickUp(driver.getId(), childId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CURRENT_PICKUP_NOT_FOUND));
        try {
            pickUpService.saveStartOrEndPickUp(pickUpHistory.getId(), image, message);
        } catch (IOException e) {
            new BusinessException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }
        return BaseResponse.success();
    }
}
