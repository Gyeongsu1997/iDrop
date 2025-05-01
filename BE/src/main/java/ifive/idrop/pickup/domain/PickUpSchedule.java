package ifive.idrop.pickup.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PickUpSchedule {
    @EmbeddedId
    private PickUpScheduleId id;
    private LocalTime startTime;

    @MapsId("pickUpSubscriptionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pick_up_subscription_id")
    private PickUpSubscription pickUpSubscription;
}
