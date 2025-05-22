package ifive.idrop.child.domain;

import ifive.idrop.common.enums.GenderConverter;
import ifive.idrop.subscription.domain.Subscription;
import ifive.idrop.common.enums.Gender;
import ifive.idrop.parent.domain.Parent;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Child {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long id;
    private String name;
    private LocalDate birthDate;
    @Convert(converter = GenderConverter.class)
    private Gender gender;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToMany(mappedBy = "child")
    private List<Subscription> subscriptionList = new ArrayList<>();
}
