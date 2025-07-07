package ifive.idrop.domain.child.service;

import ifive.idrop.domain.child.dto.ChildResponse;
import ifive.idrop.domain.child.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildService {
    private final ChildRepository childRepository;

    public List<ChildResponse> findChildren(Long parentId) {
        return childRepository.findByParentId(parentId)
                .stream()
                .map(ChildResponse::from)
                .toList();
    }
}
