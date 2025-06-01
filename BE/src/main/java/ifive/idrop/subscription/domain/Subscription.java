package ifive.idrop.subscription.domain;

import ifive.idrop.child.domain.Child;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.parent.dto.SubscriptionRequest;
import ifive.idrop.pickup.domain.PickUpHistory;
import ifive.idrop.pickup.domain.PickUpLocation;
import ifive.idrop.pickup.domain.PickUpSchedule;
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
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Subscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;
    @Column(nullable = false)
    private LocalDateTime requestDate;
    private LocalDateTime responseDate;
    private LocalDateTime expiredDate;
    @Column(name = "status_id", nullable = false)
    private SubscriptionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @OneToOne(mappedBy = "subscription", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PickUpLocation pickUpLocation;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<PickUpSchedule> pickUpScheduleList;

    @OneToMany(mappedBy = "subscription")
    private List<PickUpHistory> pickUpHistoryList = new ArrayList<>();

    public void updatePickUpLocation(PickUpLocation location) {
        this.pickUpLocation = location;
        location.subscription = this;
    }

    public Parent getParent() {
        return this.child.getParent();
    }

    public void addPickUpSchedule(PickUpSchedule schedule) {
        pickUpScheduleList.add(schedule);
    }

    public SubscriptionStatus modify(SubscriptionStatus newStatus) {
        this.status = newStatus;
        //상태가 변경된 시간
        this.responseDate = LocalDateTime.now();

        if (this.status.equals(SubscriptionStatus.PROGRESS)) {
            //modifiedDate로부터 29일 후 자정
            this.expiredDate = this.responseDate.plusDays(29)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        return this.status;
    }

    public static Subscription createSubscription(SubscriptionRequest request, Driver driver, Child child) {
        Subscription subscription = new Subscription();
        subscription.driver = driver;
        subscription.child = child;
        subscription.status = SubscriptionStatus.REQUEST;
        subscription.requestDate = LocalDateTime.now();
        subscription.pickUpScheduleList = new ArrayList<>();
        PickUpLocation pickUpLocation = PickUpLocation.createPickUpLocation(request);
        subscription.updatePickUpLocation(pickUpLocation);
        return subscription;
    }
}
