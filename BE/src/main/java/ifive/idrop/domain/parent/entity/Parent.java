package ifive.idrop.domain.parent.entity;

import ifive.idrop.domain.child.entity.Child;
import ifive.idrop.domain.user.entity.User;
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
