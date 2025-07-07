package ifive.idrop.domain.subscription.dto;

import ifive.idrop.common.enums.Day;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
public class SubscriptionRequest {
    private Long childId;
    private Long driverId;
    private LocalDate startDate;
    private String startAddress;
    private String startDetailedAddress;
    private Double startLatitude;
    private Double startLongitude;
    private String goalAddress;
    private String goalDetailedAddress;
    private Double goalLatitude;
    private Double goalLongitude;
    private Map<Day, LocalTime> schedule;
}

/**
 * {
 *     "childId": 1,
 *     "driverId": 1,
 *     "startDate: "2025-06-04",
 *     "startAddress": "서울특별시 강남구 논현동 58-3 에티버스러닝 학동캠퍼스",
 *     "startLatitude": 37.5138649,
 *     "startLongitude": 127.0295296,
 *     "goalAddress": "서울특별시 강남구 학동로31길 15 코마츠",
 *     "goalLatitude": 37.51559,
 *     "goalLongitude": 127.0316161,
 *     "schedule": {
 *         "MON": "08:30",
 *         "WED": "09:30",
 *         "FRI": "10:00"
 *     }
 * }
 */
