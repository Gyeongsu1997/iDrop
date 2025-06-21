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
    @Column(nullable = false)
    private Double startLatitude;
    @Column(nullable = false)
    private Double startLongitude;
    @Column(nullable = false)
    private String endAddress;
    @Column(nullable = false)
    private Double endLatitude;
    @Column(nullable = false)
    private Double endLongitude;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    public static PickUpLocation createPickUpLocation(Subscription subscription, SubscriptionRequest subscriptionRequest) {
        PickUpLocation pickUpLocation = new PickUpLocation();
        pickUpLocation.startAddress = subscriptionRequest.getStartAddress();
        pickUpLocation.startLatitude = subscriptionRequest.getStartLatitude();
        pickUpLocation.startLongitude = subscriptionRequest.getStartLongitude();
        pickUpLocation.endAddress = subscriptionRequest.getEndAddress();
        pickUpLocation.endLatitude = subscriptionRequest.getEndLatitude();
        pickUpLocation.endLongitude = subscriptionRequest.getEndLongitude();
        pickUpLocation.subscription = subscription;
        return pickUpLocation;
    }
}
