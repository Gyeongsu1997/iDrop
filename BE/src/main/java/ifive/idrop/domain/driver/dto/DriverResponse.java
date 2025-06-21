package ifive.idrop.domain.driver.dto;

import ifive.idrop.common.enums.Day;
import ifive.idrop.domain.driver.entity.Driver;
import ifive.idrop.domain.driver.entity.WorkSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class DriverResponse {
    private Long driverId;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String phoneNumber;
    private String imageUrl;
    private String career;
    private String introduction;
    Map<Day, Map<String, LocalTime>> schedule;

    public static DriverResponse from(Driver driver) {
        Map<Day, Map<String, LocalTime>> schedule = new HashMap<>();
        List<WorkSchedule> workScheduleList = driver.getWorkScheduleList();
        for (WorkSchedule ws : workScheduleList) {
            Map<String, LocalTime> map = new HashMap<>();
            map.put("startTime", ws.getStartTime());
            map.put("endTime", ws.getEndTime());
            schedule.put(ws.getDay(), map);
        }


        return DriverResponse.builder()
                .driverId(driver.getId())
                .name(driver.getName())
                .birthDate(driver.getBirthDate())
                .gender(driver.getGender().getDesc())
                .phoneNumber(driver.getPhoneNumber())
                .imageUrl(driver.getImageUrl())
                .career(driver.getCareer())
                .introduction(driver.getIntroduction())
                .schedule(schedule)
                .build();
    }
}
