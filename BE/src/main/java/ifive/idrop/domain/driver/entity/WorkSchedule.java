package ifive.idrop.domain.driver.entity;

import ifive.idrop.common.enums.Day;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class WorkSchedule {
    @EmbeddedId
    private WorkScheduleId id;
    private LocalTime startTime;
    private LocalTime endTime;

    @MapsId("driverId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    public static WorkSchedule createWorkHours(Driver driver, Day day, int startHour, int startMinute, int endHour, int endMinute) {
        WorkSchedule workSchedule = new WorkSchedule();
        workSchedule.id = new WorkScheduleId(driver.getId(), day);
        workSchedule.startTime = LocalTime.of(startHour, startMinute);
        workSchedule.endTime = LocalTime.of(endHour, endMinute);
        workSchedule.driver = driver;
        return workSchedule;
    }
}
