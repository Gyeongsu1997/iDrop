package ifive.idrop.entity;

import ifive.idrop.auth.domain.Authentication;
import ifive.idrop.child.domain.Child;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.parent.dto.SubscribeRequest;
import ifive.idrop.pickup.domain.PickUp;
import ifive.idrop.user.domain.User;
import ifive.idrop.user.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class PickUpSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_info_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id")
    private PickUpLocation pickUpLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    private String schedule;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "pickUpInfo")
    private PickUpSubscribe pickUpSubscribe;

    @OneToMany(mappedBy = "pickUpSubscription", cascade = CascadeType.ALL)
    private List<PickUpSchedule> pickUpScheduleList = new ArrayList<>();

    public void updatePickUpSubscribe(PickUpSubscribe pickUpSubscribe) {
        this.pickUpSubscribe = pickUpSubscribe;
        pickUpSubscribe.setPickUpSubscription(this);
    }

    public void updatePickUpLocation(PickUpLocation location) {
        this.pickUpLocation = location;
    }

    @OneToMany(mappedBy = "pickUpInfo")
    @Builder.Default
    private List<PickUp> pickUpList = new ArrayList<>();

    public Parent getParent() {
        return this.child.getParent();
    }
}
