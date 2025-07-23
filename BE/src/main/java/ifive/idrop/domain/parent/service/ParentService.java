package ifive.idrop.domain.parent.service;

import ifive.idrop.common.response.DataResponse;
import ifive.idrop.domain.driver.dto.CurrentPickUpResponse;

import ifive.idrop.domain.parent.dto.PickUpHistoryResponse;
import ifive.idrop.domain.parent.dto.ParentSubscribeInfoResponse;
import ifive.idrop.domain.pickup.entity.PickUpHistory;
import ifive.idrop.domain.parent.entity.Parent;
import ifive.idrop.domain.parent.repository.ParentRepository;
import ifive.idrop.domain.pickup.repository.PickUpRepository;
import ifive.idrop.domain.subscription.entity.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {
    private final ParentRepository parentRepository;
    private final PickUpRepository pickUpRepository;

    public DataResponse<List<CurrentPickUpResponse>> getChildRunningInfo(Parent parent) {
        List<Object[]> runningPickInfo = parentRepository.findRunningPickUpInfo(parent.getId());
        return DataResponse.success(runningPickInfo.stream()
                        .map(o -> CurrentPickUpResponse.of((Subscription) o[0], (LocalDateTime) o[1]))
                        .toList());
    }

    public DataResponse<List<PickUpHistoryResponse>> getPickUpHistoryInfo(Parent parent, long pickInfoId) {
        List<PickUpHistory> pickUpHistoryList = pickUpRepository.findPickUpByPickUpInfoIdAndParentIdOrderByReservedTime(parent.getId(), pickInfoId);
        return DataResponse.success(pickUpHistoryList.stream().map(PickUpHistoryResponse::toEntity)
                        .toList());
    }

    public List<ParentSubscribeInfoResponse> subscribeList(Long parentId) {
        List<Subscription> subscriptionList = pickUpRepository.findPickUpInfoByParentIdInTheLatestOrder(parentId);
        return subscriptionList.stream().map(ParentSubscribeInfoResponse::of).toList();
    }

    public boolean hasCurrentPickUp(Long parentId) {
        return pickUpRepository.getCurrentPickUpSize(parentId) != 0;
    }
}
