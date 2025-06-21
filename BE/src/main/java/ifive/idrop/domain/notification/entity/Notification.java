package ifive.idrop.domain.notification.entity;

import ifive.idrop.domain.driver.entity.Driver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime pickUpAlarmTime;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
