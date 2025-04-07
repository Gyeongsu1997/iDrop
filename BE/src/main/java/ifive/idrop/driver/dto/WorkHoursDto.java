package ifive.idrop.driver.dto;

import ifive.idrop.driver.domain.Driver;
import ifive.idrop.driver.domain.WorkHours;
import lombok.Getter;

@Getter
public class WorkHoursDto {
    private String day;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public WorkHours toEntity(Driver driver) {
        return WorkHours.createWorkHours(driver, day, startHour, startMinute, endHour, endMinute);
    }
}
