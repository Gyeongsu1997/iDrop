package ifive.idrop.driver.dto;

import ifive.idrop.driver.domain.Driver;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

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
    private Double starRate;

    public static DriverResponse from(Driver driver) {
        return DriverResponse.builder()
                .driverId(driver.getId())
                .name(driver.getName())
                .birthDate(driver.getBirthDate())
                .gender(driver.getGender().getDesc())
                .phoneNumber(driver.getPhoneNumber())
                .imageUrl(driver.getImageUrl())
                .career(driver.getCareer())
                .introduction(driver.getIntroduction())
                .build();
    }
}
