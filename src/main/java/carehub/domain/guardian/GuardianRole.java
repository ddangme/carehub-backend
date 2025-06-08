package carehub.domain.guardian;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GuardianRole {
    PRIMARY("주 보호자"),
    SECONDARY("부 보호자"),
    TEMPORARY("임시 보호자"),
    ;

    private final String description;
}
