package ifive.idrop.domain.child.controller;

import ifive.idrop.common.response.DataResponse;
import ifive.idrop.domain.auth.resolver.Login;
import ifive.idrop.domain.child.dto.ChildResponse;
import ifive.idrop.domain.child.service.ChildService;
import ifive.idrop.domain.parent.entity.Parent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/children")
public class ChildController {
    private final ChildService childService;

    @GetMapping
    public DataResponse<List<ChildResponse>> getChildren(@Login Parent parent) {
        List<ChildResponse> childResponseList = childService.findChildren(parent.getId());
        return DataResponse.of(childResponseList);
    }
}
