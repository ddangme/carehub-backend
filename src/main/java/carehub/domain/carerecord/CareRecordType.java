package carehub.domain.carerecord;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CareRecordType {
    FEEDING("식사"),
    SLEEP("수면"),
    DIAPER("기저귀"),
    HEALTH("건강"),
    ACTIVITY("활동"),
    OTHER("기타");

    private final String displayName;
}
