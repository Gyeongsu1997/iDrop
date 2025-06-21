package ifive.idrop.domain.pickup.entity;

import ifive.idrop.domain.driver.entity.Driver;
import ifive.idrop.domain.child.entity.Child;
import ifive.idrop.domain.parent.entity.Parent;
import ifive.idrop.domain.subscription.entity.Subscription;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PickUpHistory {
    @EmbeddedId
    private PickUpHistoryId id;
    @Column(nullable = false)
    private LocalDateTime reservedTime;
    private LocalDateTime startTime;
    private String startImage;
    private String startMessage;
    private LocalDateTime endTime;
    private String endImage;
    private String endMessage;

    @MapsId("subscriptionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    public static PickUpHistory createPickUpHistory(Subscription subscription, Short historySeq, LocalDateTime reservedTime) {
        PickUpHistory pickUpHistory = new PickUpHistory();
        pickUpHistory.id = new PickUpHistoryId(subscription.getId(), historySeq);
        pickUpHistory.reservedTime = reservedTime;
        pickUpHistory.subscription = subscription;
        return pickUpHistory;
    }

    public void updatePickUpInfo(Subscription subscription) {
        this.subscription = subscription;
        subscription.getPickUpHistoryList().add(this);
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
        return subscription.getChild();
    }

    public Driver getDriver() {
        return subscription.getDriver();
    }

    public Parent getParent() {
        return subscription.getChild().getParent();
    }
}
