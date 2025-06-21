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
    private Double startLatitude;
    private Double startLongitude;
    private String endAddress;
    private Double endLatitude;
    private Double endLongitude;
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
 *     "endAddress": "서울특별시 강남구 학동로31길 15 코마츠",
 *     "endLatitude": 37.51559,
 *     "endLongitude": 127.0316161,
 *     "schedule": {
 *         "MON": "08:30",
 *         "WED": "09:30",
 *         "FRI": "10:00"
 *     }
 * }
 */
