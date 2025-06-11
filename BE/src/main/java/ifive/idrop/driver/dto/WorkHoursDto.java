package ifive.idrop.driver.dto;

import ifive.idrop.common.enums.Day;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.domain.WorkSchedule;
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
