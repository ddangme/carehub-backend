package carehub.domain.guardian;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GuardianStatus {
    PENDING("초대 대기"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨");

    private final String description;
}
