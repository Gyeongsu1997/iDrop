package ifive.idrop.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ifive.idrop.domain.child.Child;
import ifive.idrop.domain.driver.Driver;
import ifive.idrop.domain.parent.Parent;
import ifive.idrop.domain.user.User;
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
