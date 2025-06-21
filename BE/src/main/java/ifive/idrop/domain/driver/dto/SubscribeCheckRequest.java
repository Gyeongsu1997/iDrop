package ifive.idrop.domain.driver.dto;

import lombok.Getter;

@Getter
public class SubscribeCheckRequest {
    Long pickUpInfoId;
    Integer statusCode; //0: 거절, 1: 승인
}
