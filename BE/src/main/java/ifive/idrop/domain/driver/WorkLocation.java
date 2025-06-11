package ifive.idrop.domain.driver;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
public class WorkLocation {
    @Id
    private Long driverId;
    private double latitude;
    private double longitude;
    private int radius;
    private Point point;
}
