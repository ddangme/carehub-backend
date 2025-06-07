package carehub.domain.caresubject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("M", "남성"),
    FEMALE("F", "여성"),
    OTHER("O", "기타");

    private final String code;
    private final String description;

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static Gender fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (Gender gender : Gender.values()) {
            if (gender.code.equals(code)) {
                return gender;
            }
        }

        throw new IllegalArgumentException("Unknown gender code: " + code + ". Valid codes are: M, F, O");
    }

    public static Gender fromCodeSafely(String code) {
        try {
            return fromCode(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getKoreanDescription() {
        return this.description;
    }
}