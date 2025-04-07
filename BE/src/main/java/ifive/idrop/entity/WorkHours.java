package ifive.idrop.entity;

import ifive.idrop.driver.domain.Driver;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class WorkHours {
    @Id
    private String day;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    public WorkHours(String day, int startHour, int startMinute, int endHour, int endMinute, Driver driver) {
        this.day = day;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.driver = driver;
    }
}
