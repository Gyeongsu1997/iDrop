package ifive.idrop.entity;

import ifive.idrop.auth.domain.Authentication;
import ifive.idrop.child.domain.Child;
import ifive.idrop.common.exception.BusinessException;
import ifive.idrop.common.exception.ErrorCode;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.entity.enums.PickUpStatus;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.parent.dto.SubscribeRequest;
import ifive.idrop.pickup.domain.PickUp;
import ifive.idrop.user.domain.User;
import ifive.idrop.user.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class PickUpSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pick_up_subscription_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private PickUpStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime expiredDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id")
    private PickUpLocation pickUpLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    private String schedule;

    @OneToMany(mappedBy = "pickUpSubscription", cascade = CascadeType.ALL)
    private List<PickUpSchedule> pickUpScheduleList = new ArrayList<>();

    public void updatePickUpLocation(PickUpLocation location) {
        this.pickUpLocation = location;
    }

    @OneToMany(mappedBy = "pickUpSubscription")
    @Builder.Default
    private List<PickUp> pickUpList = new ArrayList<>();

    public Parent getParent() {
        return this.child.getParent();
    }

    public PickUpStatus modify(PickUpStatus newStatus) {
        this.status = newStatus;
        //상태가 변경된 시간
        this.modifiedDate = LocalDateTime.now();

        if (this.status.equals(PickUpStatus.ACCEPT)) {
            //modifiedDate로부터 29일 후 자정
            this.expiredDate = this.modifiedDate.plusDays(29)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        return this.status;
    }
}
