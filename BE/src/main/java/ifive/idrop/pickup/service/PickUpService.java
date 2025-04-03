package ifive.idrop.pickup.service;

import ifive.idrop.parent.domain.Parent;
import ifive.idrop.pickup.domain.PickUp;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.notification.AlarmMessage;
import ifive.idrop.notification.NotificationUtill;
import ifive.idrop.pickup.repository.PickUpRepository;
import ifive.idrop.service.ImageService;
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
public class PickUpService {

    private final ImageService imageService;
    private final PickUpRepository pickUpRepository;
    private final String PICKUP_IMAGE_PATH = "image/pickup/";

    @Transactional
    public void saveStartOrEndPickUp(Long pickUpId, MultipartFile image, String message) throws IOException, ExecutionException, InterruptedException {
        PickUp pickUp = pickUpRepository.findPickUpById(pickUpId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_NOT_FOUND));
        Parent parent = pickUp.getParent();
        if (pickUp.getStartImage() == null) {
            String imageUrl = imageService.upload(image, PICKUP_IMAGE_PATH);
            pickUpRepository.savePickUpStartInfo(pickUpId, imageUrl, message);
            log.info("pickUp Start - driverId = {}, pickUpId = {}", pickUp.getDriver().getId(), pickUp.getId());

            NotificationUtill.createNotification(parent, AlarmMessage.PICK_UP_START.getTitle(),
                    AlarmMessage.PICK_UP_START.getMessage());
        } else if (pickUp.getEndImage() == null) {
            String imageUrl = imageService.upload(image, PICKUP_IMAGE_PATH);
            pickUpRepository.savePickUpEndInfo(pickUpId, imageUrl, message);
            log.info("pickUp End - driverId = {}, pickUpId = {}", pickUp.getDriver().getId(), pickUp.getId());

            NotificationUtill.createNotification(parent, AlarmMessage.PICK_UP_END.getTitle(),
                    AlarmMessage.PICK_UP_END.getMessage());
        } else {
            throw new BusinessException(ErrorCode.PICKUP_ALREADY_END);
        }
    }

    public PickUp findByPickUpId(Long pickUpId) {
        return pickUpRepository.findPickUpById(pickUpId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_NOT_FOUND));
    }

    public Optional<PickUp> findCurrentPickUp(Long driverId, Long childId) {
        List<PickUp> pickUps = pickUpRepository.findPickUpsByDriverIdWithCurrentTimeInReservedRange(driverId);
        Optional<PickUp> result = pickUps.stream()
                .filter(p -> p.getChild().getId().equals(childId))
                .findFirst();
        return result;
    }

}
