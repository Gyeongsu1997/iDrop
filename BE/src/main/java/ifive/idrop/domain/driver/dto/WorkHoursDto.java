package ifive.idrop.domain.driver.dto;

import ifive.idrop.common.enums.Day;
import ifive.idrop.domain.driver.entity.Driver;
import ifive.idrop.domain.driver.entity.WorkSchedule;
import lombok.Getter;

@Getter
public class WorkHoursDto {
    private Day day;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public WorkSchedule toEntity(Driver driver) {
        return WorkSchedule.createWorkHours(driver, day, startHour, startMinute, endHour, endMinute);
    }
}
