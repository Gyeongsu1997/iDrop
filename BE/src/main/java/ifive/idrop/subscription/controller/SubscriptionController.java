package ifive.idrop.subscription.controller;

import ifive.idrop.common.dto.BaseResponse;
import ifive.idrop.subscription.dto.SubscriptionRequest;
import ifive.idrop.subscription.dto.SubscriptionResponse;
import ifive.idrop.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public BaseResponse<String> postSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        subscriptionService.subscribe(subscriptionRequest);
        return BaseResponse.success();
    }

    @GetMapping("/drivers/{driverId}")
    public BaseResponse<List<SubscriptionResponse>> getDriverSubscriptions(@PathVariable Long driverId) {
        List<SubscriptionResponse> subscriptionResponseList = subscriptionService.findDriverSubscriptions(driverId);
        return BaseResponse.of("success", subscriptionResponseList);
    }

    @PatchMapping("/{subscriptionId}/accept")
    public BaseResponse<String> acceptSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.accept(subscriptionId);
        return BaseResponse.success();
    }

    @PatchMapping("/{subscriptionId}/reject")
    public BaseResponse<String> rejectSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.reject(subscriptionId);
        return BaseResponse.success();
    }
}
