package ifive.idrop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import ifive.idrop.child.domain.Child;
import ifive.idrop.driver.domain.Driver;
import ifive.idrop.parent.domain.Parent;
import ifive.idrop.user.domain.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NameResponse {
    String role;
    String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> children;

    public NameResponse(User user) {
        if (user instanceof Driver) {
            this.role = "기사";
        } else {
            this.role = "부모";
        }
        this.name = user.getName();
        if (user instanceof Parent parent) {
            children = new ArrayList<>();
            List<Child> childList = parent.getChildList();
            for (Child child : childList) {
                children.add(child.getName());
            }
        }
    }
}
