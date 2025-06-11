package ifive.idrop.subscription.service;

import ifive.idrop.domain.subscription.repository.SubscriptionRepository;
import ifive.idrop.domain.subscription.service.SubscriptionService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @InjectMocks
    SubscriptionService subscriptionService;
    @Mock
    SubscriptionRepository subscriptionRepository;

}