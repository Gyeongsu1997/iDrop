package ifive.idrop.subscription.controller;

import ifive.idrop.auth.resolver.Login;
import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.subscription.dto.SubscriptionRequest;
import ifive.idrop.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public BaseResponse<String> postSubscription(@Login Parent parent, @RequestBody SubscriptionRequest subscriptionRequest) {
        subscriptionService.subscribe(subscriptionRequest);
        return BaseResponse.success();
    }
}
