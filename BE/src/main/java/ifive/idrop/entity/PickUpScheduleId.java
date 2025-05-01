package ifive.idrop.entity;

import ifive.idrop.common.enums.Day;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Long pickUpSubscriptionId;
    @Enumerated(EnumType.STRING)
    private Day day;
}
