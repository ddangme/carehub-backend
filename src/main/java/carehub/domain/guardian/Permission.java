package carehub.domain.guardian;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {
    // 보호자 관리 권한
    MANAGE_GUARDIANS("보호자 관리"),

    // 케어 대상 관리 권한
    DELETE_CARE_SUBJECT("케어 대상 삭제"),
    UPDATE_CARE_SUBJECT("케어 대상 수정"),

    // 케어 활동 권한
    RECORD_ACTIVITIES("케어 활동 기록"),
    VIEW_ALL_ACTIVITIES("모든 케어 활동 조회"),

    // 일정 관리 권한
    MANAGE_SCHEDULE("일정 관리"),

    // 건강 데이터 권한
    MANAGE_HEALTH_DATA("건강 데이터 관리"),

    // 문서 관리 권한
    MANAGE_DOCUMENTS("문서 관리");

    private final String description;

    public String getKey() {
        return this.name();
    }
}
