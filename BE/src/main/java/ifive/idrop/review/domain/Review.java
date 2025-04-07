package ifive.idrop.review.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Review {
    @Id
    private Long id;
}
