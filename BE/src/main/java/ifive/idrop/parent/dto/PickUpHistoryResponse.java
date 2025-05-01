package ifive.idrop.parent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ifive.idrop.pickup.domain.PickUpHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class PickUpHistoryResponse {
    private LocalDate date;
    private String day;
    private Info info;

    public static PickUpHistoryResponse toEntity(PickUpHistory pickUpHistory) {
        String time = pickUpHistory.getReservedTime().toLocalTime().toString();
        return PickUpHistoryResponse.builder()
                .date(pickUpHistory.getReservedTime().toLocalDate())
                .day(time + " " + pickUpHistory.getReservedTime().getDayOfWeek())
                .info(Info.toEntity(pickUpHistory))
                .build();
    }
    @Builder
    @Getter
    static class Info {
        private String status;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime startTime;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String startImage;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String startAddress;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime endTime;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String endImage;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String endAddress;

        static Info toEntity(PickUpHistory pickUpHistory) {
            if (pickUpHistory.getEndTime() == null) {
                return Info.builder()
                        .status("픽업 시작")
                        .startTime(pickUpHistory.getStartTime())
                        .startImage(pickUpHistory.getStartImage())
                        .startAddress(pickUpHistory.getPickUpSubscription().getPickUpLocation().getStartAddress())
                        .endAddress(pickUpHistory.getPickUpSubscription().getPickUpLocation().getEndAddress())
                        .build();
            }

            return Info.builder()
                    .endTime(pickUpHistory.getEndTime())
                    .endImage(pickUpHistory.getEndImage())
                    .startAddress(pickUpHistory.getPickUpSubscription().getPickUpLocation().getStartAddress())
                    .endAddress(pickUpHistory.getPickUpSubscription().getPickUpLocation().getEndAddress())
                    .status("픽업 종료")
                    .build();
        }
    }
}
