package ifive.idrop.driver.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DriverSummary { //이름, 성별, 사진, 자기소개, 별점
    private Long driverId;
    private String name;
    private String gender;
    private String image;
    private String introduction;
    private Double starRate;
}
