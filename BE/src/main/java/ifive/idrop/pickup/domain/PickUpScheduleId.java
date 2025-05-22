package ifive.idrop.pickup.domain;

import ifive.idrop.common.enums.Day;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
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
    @Convert(converter = Day.Converter.class)
    @Column(columnDefinition = "char(3)")
    private Day day;
}
