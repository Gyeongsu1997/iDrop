package ifive.idrop.pickup.domain;

import ifive.idrop.driver.domain.Driver;
import ifive.idrop.child.domain.Child;
import ifive.idrop.parent.domain.Parent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class PickUpHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_id")
    private Long id;

    private LocalDateTime reservedTime;
    private LocalDateTime startTime;
    private String startImage;
    private String startMessage;
    private LocalDateTime endTime;
    private String endImage;
    private String endMessage;

    @ManyToOne
    @JoinColumn(name = "pick_up_subscription_id")
    private PickUpSubscription pickUpSubscription;

    public void updatePickUpInfo(PickUpSubscription pickUpSubscription) {
        this.pickUpSubscription = pickUpSubscription;
        pickUpSubscription.getPickUpHistoryList().add(this);
    }

    public void startPickUp(String startImage, String startMessage) {
        this.startTime = LocalDateTime.now();
        this.startImage = startImage;
        this.startMessage = startMessage;
    }

    public void endPickUp(String endImage, String endMessage) {
        this.endTime = LocalDateTime.now();
        this.endImage = endImage;
        this.endMessage = endMessage;
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
