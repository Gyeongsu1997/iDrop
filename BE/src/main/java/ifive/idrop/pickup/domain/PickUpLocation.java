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
    private Subscription subscription;
}
