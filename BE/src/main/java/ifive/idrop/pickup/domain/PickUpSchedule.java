package ifive.idrop.pickup.domain;

import ifive.idrop.common.enums.Day;
import ifive.idrop.subscription.domain.Subscription;
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

    @MapsId("subscriptionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    public Day getDay() {
        return this.id.getDay();
    }
}
