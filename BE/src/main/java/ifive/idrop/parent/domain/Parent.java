package ifive.idrop.parent.domain;

import ifive.idrop.child.domain.Child;
import ifive.idrop.user.domain.User;
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
