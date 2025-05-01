package ifive.idrop.driver.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class DriverDetailResponse {
    private Long driverId;
    private String name;
    private String phoneNumber;
    private String gender;
    private LocalDate birth;
    private String image;
    private String career;
    private String introduction;
    private Double starRate;
}
