package carehub.web.dto.caresubject;

import carehub.domain.caresubject.CareSubject;
import carehub.domain.caresubject.CareSubjectType;
import carehub.domain.caresubject.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// 케어 대상 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareSubjectResponse {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private Gender gender;
    private String bloodType;
    private BigDecimal birthWeight;
    private BigDecimal birthHeight;
    private String profileImageUrl;
    private Map<String, Object> additionalInfo;
    private Boolean isActive;

    // 케어 대상 타입
    private CareSubjectType subjectType;

    // 계산된 필드
    private int ageInMonths;
    private long ageInDays;

    // 생성자 정보
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 보호자 목록 (간단한 정보만)
    private List<GuardianSummary> guardians;

    // 신생아 전용 필드들
    private Integer birthWeightGrams;
    private Integer birthHeightCm;
    private Integer headCircumferenceCm;
    private Integer gestationalAgeWeeks;
    private String deliveryType;
    private String allergies;
    private String specialCareNeeds;
    private LocalDate lastCheckupDate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuardianSummary {
        private Long id;
        private String userName;
        private String userEmail;
        private String role;
        private String status;
    }

    public static CareSubjectResponse from(CareSubject careSubject) {
        List<GuardianSummary> guardianSummaries = careSubject.getGuardians().stream()
                .map(guardian -> GuardianSummary.builder()
                        .id(guardian.getId())
                        .userName(guardian.getUser().getName())
                        .userEmail(guardian.getUser().getEmail())
                        .role(guardian.getRole().name())
                        .status(guardian.getStatus().name())
                        .build())
                .toList();

        return CareSubjectResponse.builder()
                .id(careSubject.getId())
                .name(careSubject.getName())
                .birthDate(careSubject.getBirthDate())
                .gender(careSubject.getGender())
                .bloodType(careSubject.getBloodType())
                .birthWeight(careSubject.getBirthWeight())
                .birthHeight(careSubject.getBirthHeight())
                .profileImageUrl(careSubject.getProfileImageUrl())
                .additionalInfo(careSubject.getAdditionalInfo())
                .isActive(careSubject.getIsActive())
                .subjectType(careSubject.getSubjectType())
                .ageInMonths(careSubject.getAgeInMonths())
                .ageInDays(careSubject.getAgeInDays())
                .createdByName(careSubject.getCreatedBy().getName())
                .createdAt(careSubject.getCreatedAt())
                .updatedAt(careSubject.getUpdatedAt())
                .guardians(guardianSummaries)
                // 신생아 전용 필드들
                .birthWeightGrams(careSubject.getBirthWeightGrams())
                .birthHeightCm(careSubject.getBirthHeightCm())
                .headCircumferenceCm(careSubject.getHeadCircumferenceCm())
                .gestationalAgeWeeks(careSubject.getGestationalAgeWeeks())
                .deliveryType(careSubject.getDeliveryType())
                .allergies(careSubject.getAllergies())
                .specialCareNeeds(careSubject.getSpecialCareNeeds())
                .lastCheckupDate(careSubject.getLastCheckupDate())
                .build();
    }
}
