package ifive.idrop.driver.domain;

import ifive.idrop.common.enums.Day;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class WorkHours {
    @EmbeddedId
    private WorkHoursId id;
    private LocalTime startTime;
    private LocalTime endTime;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    public static WorkHours createWorkHours(Driver driver, String day, int startHour, int startMinute, int endHour, int endMinute) {
        WorkHours workHours = new WorkHours();
        workHours.id = new WorkHoursId(driver.getId(), Day.getDayEnum(day));
        workHours.startTime = LocalTime.of(startHour, startMinute);
        workHours.endTime = LocalTime.of(endHour, endMinute);
        workHours.driver = driver;
        return workHours;
    }
}
