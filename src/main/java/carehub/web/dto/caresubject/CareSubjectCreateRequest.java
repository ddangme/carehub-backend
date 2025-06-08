package carehub.web.dto.caresubject;

import carehub.domain.caresubject.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareSubjectCreateRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotNull(message = "생년월일은 필수입니다")
    @Past(message = "생년월일은 과거 날짜여야 합니다")
    private LocalDate birthDate;

    private Gender gender;

    private String bloodType;

    private BigDecimal birthWeight;

    private BigDecimal birthHeight;

    private String profileImageUrl;

    private Map<String, Object> additionalInfo;

    private Integer birthWeightGrams;

    private Integer birthHeightCm;

    private Integer headCircumferenceCm;

    private Integer gestationalAgeWeeks;

    private String deliveryType;

    private String allergies;

    private String specialCareNeeds;

    private LocalDate lastCheckupDate;

    @Override
    public String toString() {
        return "CareSubjectCreateRequest{" +
                "name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", gender=" + (gender != null ? gender.getCode() : "null") +
                ", bloodType='" + bloodType + '\'' +
                ", birthWeight=" + birthWeight +
                ", birthHeight=" + birthHeight +
                ", profileImageUrl='" + (profileImageUrl != null ? "있음" : "없음") + '\'' +
                ", additionalInfo=" + (additionalInfo != null ? additionalInfo.size() + "개 항목" : "없음") +
                ", birthWeightGrams=" + birthWeightGrams +
                ", birthHeightCm=" + birthHeightCm +
                ", headCircumferenceCm=" + headCircumferenceCm +
                ", gestationalAgeWeeks=" + gestationalAgeWeeks +
                ", deliveryType='" + deliveryType + '\'' +
                ", allergies='" + allergies + '\'' +
                ", specialCareNeeds='" + specialCareNeeds + '\'' +
                ", lastCheckupDate=" + lastCheckupDate +
                '}';
    }

    public void validateRequest() {
        log.debug("케어 대상 생성 요청 유효성 검증 시작");

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다");
        }

        if (birthDate == null) {
            throw new IllegalArgumentException("생년월일은 필수입니다");
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("생년월일은 과거 날짜여야 합니다");
        }

        // 신생아 특화 필드 검증
        if (birthWeightGrams != null && birthWeightGrams <= 0) {
            throw new IllegalArgumentException("출생 체중은 0보다 커야 합니다");
        }

        if (birthHeightCm != null && birthHeightCm <= 0) {
            throw new IllegalArgumentException("출생 신장은 0보다 커야 합니다");
        }

        if (gestationalAgeWeeks != null && (gestationalAgeWeeks < 20 || gestationalAgeWeeks > 45)) {
            throw new IllegalArgumentException("재태 기간은 20-45주 사이여야 합니다");
        }

        log.debug("케어 대상 생성 요청 유효성 검증 완료");
    }

    public void cleanEmptyFields() {
        log.debug("빈 문자열 필드 정리 시작");

        if (bloodType != null && bloodType.trim().isEmpty()) {
            bloodType = null;
        }

        if (profileImageUrl != null && profileImageUrl.trim().isEmpty()) {
            profileImageUrl = null;
        }

        if (deliveryType != null && deliveryType.trim().isEmpty()) {
            deliveryType = null;
        }

        if (allergies != null && allergies.trim().isEmpty()) {
            allergies = null;
        }

        if (specialCareNeeds != null && specialCareNeeds.trim().isEmpty()) {
            specialCareNeeds = null;
        }

        log.debug("빈 문자열 필드 정리 완료");
    }
}