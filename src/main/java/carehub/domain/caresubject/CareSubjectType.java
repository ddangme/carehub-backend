package carehub.domain.caresubject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CareSubjectType {
    DEFAULT("일반", "일반 케어 대상"),
    INFANT("신생아", "0-24개월 영아"),
    CHILD("어린이", "2-12세 어린이"),
    ELDERLY("노인", "노인 케어 대상"),
    PET("반려동물", "반려동물");

    private final String displayName;
    private final String description;
}
