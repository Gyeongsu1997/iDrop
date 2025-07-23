package ifive.idrop.domain.subscription.controller;

import ifive.idrop.common.response.DataResponse;
import ifive.idrop.domain.subscription.dto.SubscriptionRequest;
import ifive.idrop.domain.subscription.dto.SubscriptionResponse;
import ifive.idrop.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public DataResponse<?> postSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        subscriptionService.subscribe(subscriptionRequest);
        return DataResponse.success();
    }

    @GetMapping("/drivers/{driverId}")
    public DataResponse<List<SubscriptionResponse>> getDriverSubscriptions(@PathVariable Long driverId) {
        List<SubscriptionResponse> subscriptionResponseList = subscriptionService.findDriverSubscriptions(driverId);
        return DataResponse.success(subscriptionResponseList);
    }

    @PatchMapping("/{subscriptionId}/accept")
    public DataResponse<?> acceptSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.accept(subscriptionId);
        return DataResponse.success();
    }

    @PatchMapping("/{subscriptionId}/reject")
    public DataResponse<?> rejectSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.reject(subscriptionId);
        return DataResponse.success();
    }
}
