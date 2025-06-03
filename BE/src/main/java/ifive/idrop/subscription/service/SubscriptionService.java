package ifive.idrop.subscription.service;

import ifive.idrop.child.domain.Child;
import ifive.idrop.child.repository.ChildRepository;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.repository.DriverRepository;
import ifive.idrop.subscription.domain.Subscription;
import ifive.idrop.subscription.dto.SubscriptionRequest;
import ifive.idrop.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {
    private final ChildRepository childRepository;
    private final DriverRepository driverRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void subscribe(SubscriptionRequest subscriptionRequest) {
        Driver driver = driverRepository.findById(subscriptionRequest.getDriverId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DRIVER_NOT_EXIST));
        Child child = childRepository.findById(subscriptionRequest.getChildId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_EXIST));

        Subscription subscription = Subscription.createSubscription(subscriptionRequest, child, driver);
        subscriptionRepository.save(subscription);
    }
}
