package ifive.idrop.domain.pickup.service;

import ifive.idrop.domain.parent.Parent;
import ifive.idrop.domain.pickup.PickUpHistory;
import ifive.idrop.domain.pickup.repository.PickUpRepository;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.domain.notification.AlarmMessage;
import ifive.idrop.domain.notification.NotificationUtill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PickUpService {
    private final PickUpRepository pickUpRepository;

    private final ImageService imageService;
    private final String PICKUP_IMAGE_PATH = "image/pickup/";

    @Transactional
    public void saveStartOrEndPickUp(Long pickUpId, MultipartFile image, String message) throws IOException, ExecutionException, InterruptedException {
        PickUpHistory pickUpHistory = pickUpRepository.findPickUpById(pickUpId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_NOT_FOUND));
        Parent parent = pickUpHistory.getParent();
        if (pickUpHistory.getStartImage() == null) {
            String imageUrl = imageService.upload(image, PICKUP_IMAGE_PATH);
            pickUpRepository.savePickUpStartInfo(pickUpId, imageUrl, message);
            log.info("pickUp Start - driverId = {}, pickUpId = {}", pickUpHistory.getDriver().getId(), pickUpHistory.getId());

            NotificationUtill.createNotification(parent, AlarmMessage.PICK_UP_START.getTitle(),
                    AlarmMessage.PICK_UP_START.getMessage());
        } else if (pickUpHistory.getEndImage() == null) {
            String imageUrl = imageService.upload(image, PICKUP_IMAGE_PATH);
            pickUpRepository.savePickUpEndInfo(pickUpId, imageUrl, message);
            log.info("pickUp End - driverId = {}, pickUpId = {}", pickUpHistory.getDriver().getId(), pickUpHistory.getId());

            NotificationUtill.createNotification(parent, AlarmMessage.PICK_UP_END.getTitle(),
                    AlarmMessage.PICK_UP_END.getMessage());
        } else {
            throw new BusinessException(ErrorCode.PICKUP_ALREADY_END);
        }
    }

    public Optional<PickUpHistory> findCurrentPickUp(Long driverId, Long childId) {
        List<PickUpHistory> pickUpHistories = pickUpRepository.findPickUpsByDriverIdWithCurrentTimeInReservedRange(driverId);
        Optional<PickUpHistory> result = pickUpHistories.stream()
                .filter(p -> p.getChild().getId().equals(childId))
                .findFirst();
        return result;
    }
}
