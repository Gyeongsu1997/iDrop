package ifive.idrop.pickup.domain;

import ifive.idrop.child.domain.Child;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.pickup.domain.enums.PickUpStatus;
import ifive.idrop.parent.domain.Parent;
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
public class Subscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private PickUpStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime modifiedDate;
    private LocalDateTime expiredDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @OneToOne(mappedBy = "subscription", fetch = FetchType.LAZY)
    private PickUpLocation pickUpLocation;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<PickUpSchedule> pickUpScheduleList;

    @OneToMany(mappedBy = "subscription")
    private List<PickUpHistory> pickUpHistoryList = new ArrayList<>();

    public void updatePickUpLocation(PickUpLocation location) {
        this.pickUpLocation = location;
    }

    public Parent getParent() {
        return this.child.getParent();
    }

    public void addPickUpSchedule(PickUpSchedule schedule) {
        pickUpScheduleList.add(schedule);
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
