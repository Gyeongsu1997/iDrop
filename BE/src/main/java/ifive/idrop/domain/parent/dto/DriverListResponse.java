package ifive.idrop.domain.parent.dto;

import ifive.idrop.domain.driver.dto.DriverSummary;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DriverListResponse {
    private final List<DriverSummary> drivers = new ArrayList<>();

    public void addDriverSummary(DriverSummary driverSummary) {
        drivers.add(driverSummary);
    }
}
