package ifive.idrop.subscription.domain;

import ifive.idrop.child.domain.Child;
import ifive.idrop.common.enums.Day;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.pickup.domain.PickUpHistory;
import ifive.idrop.pickup.domain.PickUpLocation;
import ifive.idrop.pickup.domain.PickUpSchedule;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.pickup.domain.PickUpScheduleId;
import ifive.idrop.subscription.dto.SubscriptionRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
        subscription.status = SubscriptionStatus.REQUEST;
        subscription.child = child;
        subscription.driver = driver;
        subscription.pickUpLocation = PickUpLocation.createPickUpLocation(subscription, subscriptionRequest);

        Map<Day, String> schedule = subscriptionRequest.getSchedule();
        for (Map.Entry<Day, String> entry : schedule.entrySet()) {
            Day day = entry.getKey();
            StringTokenizer st = new StringTokenizer(entry.getValue(), ":");
            int hour = Integer.parseInt(st.nextToken());
            int min = Integer.parseInt(st.nextToken());
            subscription.addPickUpSchedule(new PickUpSchedule(new PickUpScheduleId(subscription.getId(), day), LocalTime.of(hour, min), subscription));
        }
        return subscription;
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

    public void reject() {
        if (this.status != SubscriptionStatus.REQUEST) {
            throw new RuntimeException();
        }
        this.status = SubscriptionStatus.REJECTED;
        this.responseDate = LocalDateTime.now();
    }
}
