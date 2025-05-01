package ifive.idrop.driver.domain;

import ifive.idrop.driver.dto.DriverInformation;
import ifive.idrop.driver.dto.DriverDetailResponse;
import ifive.idrop.driver.dto.DriverSummary;
import ifive.idrop.driver.dto.WorkHoursDto;
import ifive.idrop.pickup.domain.Subscription;
import ifive.idrop.user.domain.User;
import ifive.idrop.common.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DiscriminatorValue("D")
public class Driver extends User {
    @Lob
    private String career;
    @Lob
    private String introduction;
    private Double starRate;

    @OneToMany(mappedBy = "driver")
    private List<Subscription> subscriptionList = new ArrayList<>();

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<WorkHours> workHoursList = new ArrayList<>();

    public void addAdditionalInfo(DriverInformation info) {
        this.gender = (info.getGender() != null) ? Gender.of(info.getGender()) : this.gender;
        this.birthDate = (info.getBirth() != null) ? info.getBirth() : this.birthDate;
        this.imageUrl = (info.getImage() != null) ? info.getImage() : this.imageUrl;
        this.career = (info.getCareer() != null) ? info.getCareer() : this.career;
        this.introduction = (info.getIntroduction() != null) ? info.getIntroduction() : this.introduction;

        List<WorkHoursDto> availableTime = info.getAvailableTime();
        for (WorkHoursDto workHoursDto : availableTime) {
            workHoursList.add(workHoursDto.toEntity(this));
        }
    }

    public DriverSummary getSummary() {
        return DriverSummary.builder()
                .driverId(this.getId())
                .name(this.getName())
                .gender((this.gender != null) ? this.gender.getLabel() : null)
                .image(this.getImageUrl())
                .introduction(this.getIntroduction())
                .starRate(this.getStarRate())
                .build();
    }

    public DriverDetailResponse getDetail() {
        return DriverDetailResponse.builder()
                .driverId(this.getId())
                .name(this.getName())
                .phoneNumber(this.getPhoneNumber())
                .gender((this.gender != null) ? this.gender.getLabel() : null)
                .birth(this.getBirthDate())
                .image(this.getImageUrl())
                .career(this.getCareer())
                .introduction(this.getIntroduction())
                .starRate(this.getStarRate())
                .build();
    }
}
