package ifive.idrop.parent.domain;

import ifive.idrop.entity.Child;
import ifive.idrop.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Parent extends Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id")
    private Long id;
    @OneToMany(mappedBy = "parent")
    private List<Child> childList = new ArrayList<>();
}
