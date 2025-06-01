package ifive.idrop.pickup.domain;

import ifive.idrop.common.enums.Day;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PickUpScheduleId implements Serializable {
    private Long subscriptionId;
    @Enumerated(EnumType.STRING)
    private Day day;
}
