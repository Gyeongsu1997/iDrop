package ifive.idrop.driver.domain;

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
public class WorkHoursId implements Serializable {
    private Long driverId;
    @Convert(converter = Day.Converter.class)
    @Column(columnDefinition = "char(3)")
    private Day day;
}
