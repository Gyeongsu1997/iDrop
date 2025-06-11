package ifive.idrop.domain.driver;

import jakarta.persistence.*;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
public class WorkLocation {
    @Id
    private Long driverId;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private Point point;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
