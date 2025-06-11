package ifive.idrop.domain.driver;

import ifive.idrop.domain.driver.dto.DriverInformation;
import ifive.idrop.domain.driver.dto.WorkHoursDto;
import ifive.idrop.domain.driver.dto.DriverSummary;
import ifive.idrop.domain.subscription.Subscription;
import ifive.idrop.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DiscriminatorValue("D")
public class Driver extends User {
    private String career;
    private String introduction;

    @OneToMany(mappedBy = "driver")
    private List<Subscription> subscriptionList = new ArrayList<>();

    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private WorkLocation workLocation;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<WorkSchedule> workScheduleList = new ArrayList<>();

    public void addAdditionalInfo(DriverInformation info) {
//        this.gender = (info.getGender() != null) ? Gender.of(info.getGender()) : this.gender;
        this.birthDate = (info.getBirth() != null) ? info.getBirth() : this.birthDate;
        this.imageUrl = (info.getImage() != null) ? info.getImage() : this.imageUrl;
        this.career = (info.getCareer() != null) ? info.getCareer() : this.career;
        this.introduction = (info.getIntroduction() != null) ? info.getIntroduction() : this.introduction;

        List<WorkHoursDto> availableTime = info.getAvailableTime();
        for (WorkHoursDto workHoursDto : availableTime) {
            workScheduleList.add(workHoursDto.toEntity(this));
        }
    }

    public DriverSummary getSummary() {
        return DriverSummary.builder()
                .driverId(this.getId())
                .name(this.getName())
//                .gender((this.gender != null) ? this.gender.getLabel() : null)
                .image(this.getImageUrl())
                .introduction(this.getIntroduction())
                .build();
    }
}
