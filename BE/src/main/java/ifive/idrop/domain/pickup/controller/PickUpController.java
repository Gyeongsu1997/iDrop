package ifive.idrop.domain.pickup.controller;

import ifive.idrop.domain.auth.resolver.Login;
import ifive.idrop.common.response.DataResponse;
import ifive.idrop.domain.driver.entity.Driver;
import ifive.idrop.domain.pickup.entity.PickUpHistory;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.domain.pickup.service.PickUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class PickUpController {
    private final PickUpService pickUpService;


    @PostMapping("/driver/pickup")
    public DataResponse<?> startOrEndPickUp(@Login Driver driver, Long childId, @ModelAttribute MultipartFile image, String message) throws ExecutionException, InterruptedException {
        PickUpHistory pickUpHistory = pickUpService.findCurrentPickUp(driver.getId(), childId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CURRENT_PICKUP_NOT_FOUND));
        try {
//            pickUpService.saveStartOrEndPickUp(pickUpHistory.getId(), image, message);
            pickUpService.saveStartOrEndPickUp(1L, image, message);
        } catch (IOException e) {
            new BusinessException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }
        return DataResponse.success();
    }
}
