package ifive.idrop.domain.parent;

import ifive.idrop.domain.child.Child;
import ifive.idrop.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DiscriminatorValue("P")
public class Parent extends User {
    @OneToMany(mappedBy = "parent")
    private List<Child> childList = new ArrayList<>();
}
