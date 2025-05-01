package ifive.idrop.parent.dto;

import ifive.idrop.common.enums.Day;
import lombok.Getter;

import java.util.Map;

@Getter
public class SubscriptionRequest {
    private Long driverId;
    private String startAddress;
    private Double startLongitude;
    private Double startLatitude;
    private String endAddress;
    private Double endLongitude;
    private Double endLatitude;
    private Map<Day, Map<String, Integer>> schedule;
}
