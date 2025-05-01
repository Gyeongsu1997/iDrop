package ifive.idrop.driver.domain;

import ifive.idrop.driver.dto.DriverInformation;
import ifive.idrop.driver.dto.DriverDetailResponse;
import ifive.idrop.driver.dto.DriverSummary;
import ifive.idrop.driver.dto.WorkHoursDto;
import ifive.idrop.entity.PickUpSubscription;
import ifive.idrop.user.domain.User;
import ifive.idrop.common.enums.Gender;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static ifive.idrop.util.ScheduleUtils.*;

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
    private List<PickUpSubscription> pickUpSubscriptionList = new ArrayList<>();

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
            if (!DAY_OF_WEEKS.contains(workHoursDto.getDay())) {
                throw new BusinessException(ErrorCode.INVALID_DAY_OF_WEEK);
            }
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
                .numberOfReviews(100) //TODO 후기 개수, 나중에 후기 테이블을 만들면 실제 개수로 수정 예정
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
                .numberOfReviews(100) //TODO 후기 개수, 나중에 후기 테이블을 만들면 실제 개수로 수정 예정
                .build();
    }
}
