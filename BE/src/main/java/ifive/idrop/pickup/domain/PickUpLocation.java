package ifive.idrop.pickup.domain;

import ifive.idrop.parent.dto.SubscriptionRequest;
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
    private Double startLongitude;

    @Column(nullable = false)
    private Double startLatitude;

    @Column(nullable = false)
    private String endAddress;

    @Column(nullable = false)
    private Double endLongitude;

    @Column(nullable = false)
    private Double endLatitude;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    Subscription subscription;

    public static PickUpLocation createPickUpLocation(SubscriptionRequest subscriptionRequest) {
        PickUpLocation pickUpLocation = new PickUpLocation();
        pickUpLocation.startAddress = subscriptionRequest.getStartAddress();
        pickUpLocation.startLatitude = subscriptionRequest.getStartLatitude();
        pickUpLocation.startLongitude = subscriptionRequest.getStartLongitude();
        pickUpLocation.endAddress = subscriptionRequest.getEndAddress();
        pickUpLocation.endLatitude = subscriptionRequest.getEndLatitude();
        pickUpLocation.endLongitude = subscriptionRequest.getEndLongitude();
        return pickUpLocation;
    }
}
