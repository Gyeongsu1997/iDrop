package ifive.idrop.pickup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PickUpLocation {
    @Id
    private Long subscriptionId;
    private String startAddress;
    private Double startLongitude;
    private Double startLatitude;
    private String endAddress;
    private Double endLongitude;
    private Double endLatitude;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
}
