package ifive.idrop.domain.child.dto;

import ifive.idrop.domain.child.entity.Child;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ChildResponse {
    private Long childId;
    private Long parentId;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String imageUrl;

    public static ChildResponse from(Child child) {
        return ChildResponse.builder()
                .childId(child.getId())
                .parentId(child.getParent().getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .gender(child.getGender().getDesc())
                .imageUrl(child.getImageUrl())
                .build();
    }
}
