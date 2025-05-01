package ifive.idrop.pickup.domain;

import ifive.idrop.driver.domain.Driver;
import ifive.idrop.child.domain.Child;
import ifive.idrop.entity.PickUpSubscription;
import ifive.idrop.parent.domain.Parent;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class PickUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_id")
    private Long id;

    private String startImage;
    private String endImage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime reservedTime;
    private String startMessage;
    private String endMessage;

    @ManyToOne
    @JoinColumn(name = "pickup_info_id")
    private PickUpSubscription pickUpSubscription;

    public void updatePickUpInfo(PickUpSubscription pickUpSubscription) {
        this.pickUpSubscription = pickUpSubscription;
        pickUpSubscription.getPickUpList().add(this);
    }

    public void updateStartPickUpInfo(String startImage, String startMessage) {
        this.startImage = startImage;
        this.startTime = LocalDateTime.now();
        this.startMessage = startMessage;
    }

    public void updateEndPickUpInfo(String endImage, String endMessage) {
        this.endImage = endImage;
        this.endTime = LocalDateTime.now();
        this.endMessage = endMessage;
    }

    public boolean isDriver(Driver driver) {
        return pickUpSubscription.getDriver().getId().equals(driver.getId());
    }

    public Child getChild() {
        return pickUpSubscription.getChild();
    }

    public Driver getDriver() {
        return pickUpSubscription.getDriver();
    }

    public Parent getParent() {
        return pickUpSubscription.getChild().getParent();
    }
}
