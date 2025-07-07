package ifive.idrop.domain.pickup.entity;

import ifive.idrop.domain.subscription.entity.Subscription;
import ifive.idrop.domain.subscription.dto.SubscriptionRequest;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PickUpLocation {
    @Id
    private Long subscriptionId;
    @Column(nullable = false)
    private String startAddress;
    private String startDetailedAddress;
    @Column(nullable = false)
    private Double startLatitude;
    @Column(nullable = false)
    private Double startLongitude;
    @Column(nullable = false)
    private String goalAddress;
    private String goalDetailedAddress;
    @Column(nullable = false)
    private Double goalLatitude;
    @Column(nullable = false)
    private Double goalLongitude;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    public static PickUpLocation createPickUpLocation(Subscription subscription, SubscriptionRequest subscriptionRequest) {
        PickUpLocation pickUpLocation = new PickUpLocation();
        pickUpLocation.startAddress = subscriptionRequest.getStartAddress();
        pickUpLocation.startDetailedAddress = subscriptionRequest.getStartDetailedAddress();
        pickUpLocation.startLatitude = subscriptionRequest.getStartLatitude();
        pickUpLocation.startLongitude = subscriptionRequest.getStartLongitude();
        pickUpLocation.goalAddress = subscriptionRequest.getGoalAddress();
        pickUpLocation.goalDetailedAddress = subscriptionRequest.getGoalDetailedAddress();
        pickUpLocation.goalLatitude = subscriptionRequest.getGoalLatitude();
        pickUpLocation.goalLongitude = subscriptionRequest.getGoalLongitude();
        pickUpLocation.subscription = subscription;
        return pickUpLocation;
    }
}
