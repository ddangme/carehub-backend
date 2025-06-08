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
    private Integer ageYears;
    private Integer ageDays;

    // 생성자 정보
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 임시: 프론트엔드 호환성을 위한 주 보호자 정보 (실제로는 createdBy와 동일)
    private MainCaregiverInfo mainCaregiver;

    // 보호자 목록 (간단한 정보만)
    private List<GuardianSummary> guardians;

    // 추가 보호자 목록 (PRIMARY 보호자 제외한 나머지)
    private List<CaregiverInfo> caregivers;

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
    public static class MainCaregiverInfo {
        private Long id;
        private String name;
        private String email;
        private String profileImageUrl;
    }

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaregiverInfo {
        private Long id;
        private String name;
        private String email;
        private String profileImageUrl;
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

        // 추가 보호자 목록 (PRIMARY 제외)
        List<CaregiverInfo> caregivers = careSubject.getGuardians().stream()
                .filter(guardian -> guardian.getRole() != carehub.domain.guardian.GuardianRole.PRIMARY)
                .filter(guardian -> guardian.getStatus() == carehub.domain.guardian.GuardianStatus.ACCEPTED)
                .map(guardian -> CaregiverInfo.builder()
                        .id(guardian.getUser().getId())
                        .name(guardian.getUser().getName())
                        .email(guardian.getUser().getEmail())
                        .profileImageUrl(guardian.getUser().getProfileImageUrl())
                        .build())
                .toList();

        // 임시: mainCaregiver는 createdBy와 동일하게 설정 (프론트엔드 호환성 유지)
        MainCaregiverInfo mainCaregiver = MainCaregiverInfo.builder()
                .id(careSubject.getCreatedBy().getId())
                .name(careSubject.getCreatedBy().getName())
                .email(careSubject.getCreatedBy().getEmail())
                .profileImageUrl(careSubject.getCreatedBy().getProfileImageUrl())
                .build();

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
                .ageYears(careSubject.getAgeInMonths() / 12) // 년 단위 나이 계산
                .ageDays((int) careSubject.getAgeInDays()) // int 타입으로 변환
                .createdByName(careSubject.getCreatedBy().getName())
                .createdAt(careSubject.getCreatedAt())
                .updatedAt(careSubject.getUpdatedAt())
                .mainCaregiver(mainCaregiver) // 임시: createdBy 정보 사용
                .guardians(guardianSummaries)
                .caregivers(caregivers)
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
