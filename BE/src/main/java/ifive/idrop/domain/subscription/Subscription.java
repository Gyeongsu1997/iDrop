package ifive.idrop.domain.subscription;

import ifive.idrop.domain.child.Child;
import ifive.idrop.common.enums.Day;
import ifive.idrop.domain.driver.Driver;
import ifive.idrop.domain.pickup.PickUpHistory;
import ifive.idrop.domain.pickup.PickUpLocation;
import ifive.idrop.domain.pickup.PickUpSchedule;
import ifive.idrop.domain.parent.Parent;
import ifive.idrop.domain.pickup.PickUpScheduleId;
import ifive.idrop.domain.subscription.dto.SubscriptionRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDateTime expiredDate;
    @Convert(converter = SubscriptionStatus.Converter.class)
    @Column(name = "status_id", columnDefinition = "tinyint unsigned", nullable = false)
    private SubscriptionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @OneToOne(mappedBy = "subscription", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PickUpLocation pickUpLocation;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<PickUpSchedule> pickUpScheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "subscription")
    private List<PickUpHistory> pickUpHistoryList = new ArrayList<>();

    public static Subscription createSubscription(SubscriptionRequest subscriptionRequest, Child child, Driver driver) {
        Subscription subscription = new Subscription();
        subscription.requestDate = LocalDateTime.now();
        subscription.startDate = subscriptionRequest.getStartDate();
        subscription.status = SubscriptionStatus.REQUEST;
        subscription.child = child;
        subscription.driver = driver;
        subscription.pickUpLocation = PickUpLocation.createPickUpLocation(subscription, subscriptionRequest);

        Map<Day, LocalTime> schedule = subscriptionRequest.getSchedule();
        for (Map.Entry<Day, LocalTime> entry : schedule.entrySet()) {
            Day day = entry.getKey();
            LocalTime startTime = entry.getValue();
            subscription.addPickUpSchedule(new PickUpSchedule(new PickUpScheduleId(subscription.getId(), day), startTime, subscription));
        }
        return subscription;
    }

    public Parent getParent() {
        return this.child.getParent();
    }

    public void addPickUpSchedule(PickUpSchedule schedule) {
        pickUpScheduleList.add(schedule);
    }

    public void accept() {
        if (this.status != SubscriptionStatus.REQUEST) {
            throw new RuntimeException();
        }
        this.status = SubscriptionStatus.PROGRESS;
        this.responseDate = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != SubscriptionStatus.REQUEST) {
            throw new RuntimeException();
        }
        this.status = SubscriptionStatus.REJECTED;
        this.responseDate = LocalDateTime.now();
    }
}
