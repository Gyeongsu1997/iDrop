package ifive.idrop.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
    DRIVER('D'),
    PARENT('P');

    private final Character value;

    public static Role of(Character value) {
        return Arrays.stream(Role.values())
                .filter(r -> r.getValue().equals(value))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
