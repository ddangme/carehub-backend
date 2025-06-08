package carehub.domain.caresubject;

import carehub.domain.guardian.Guardian;
import carehub.domain.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "care_subjects", indexes = {
        @Index(name = "idx_care_subject_created_by", columnList = "created_by"),
        @Index(name = "idx_care_subject_active", columnList = "is_active"),
        @Index(name = "idx_care_subject_type", columnList = "subject_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 10)
    private Gender gender;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(name = "birth_weight", precision = 5, scale = 2)
    private BigDecimal birthWeight;

    @Column(name = "birth_height", precision = 5, scale = 2)
    private BigDecimal birthHeight;

    // 케어 대상 유형 추가 (필수)
    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type", nullable = false, length = 20)
    @Builder.Default
    private CareSubjectType subjectType = CareSubjectType.INFANT;

    @Type(JsonType.class)
    @Column(name = "additional_info", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> additionalInfo = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // 임시: main_caregiver_id 컬럼 대응 (항상 createdBy와 동일하게 설정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_caregiver_id", nullable = false)
    private User mainCaregiver;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "careSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<Guardian> guardians = new ArrayList<>();

    // 신생아 전용 필드들
    @Column(name = "birth_weight_grams")
    private Integer birthWeightGrams;

    @Column(name = "birth_height_cm")
    private Integer birthHeightCm;

    @Column(name = "head_circumference_cm")
    private Integer headCircumferenceCm;

    @Column(name = "gestational_age_weeks")
    private Integer gestationalAgeWeeks;

    @Column(name = "delivery_type", length = 20)
    private String deliveryType;

    @Column(name = "allergies", length = 500)
    private String allergies;

    @Column(name = "special_care_needs", length = 1000)
    private String specialCareNeeds;

    @Column(name = "last_checkup_date")
    private LocalDate lastCheckupDate;

    public void addGuardian(Guardian guardian) {
        guardians.add(guardian);
        guardian.setCareSubject(this);
    }

    public void removeGuardian(Guardian guardian) {
        guardians.remove(guardian);
        guardian.setCareSubject(null);
    }

    public int getAgeInMonths() {
        LocalDate now = LocalDate.now();
        return (int) java.time.Period.between(birthDate, now).toTotalMonths();
    }

    public long getAgeInDays() {
        LocalDate now = LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(birthDate, now);
    }

    /**
     * 케어 대상 유형 결정 로직
     * 신생아 전용 필드가 하나라도 있으면 INFANT, 없으면 DEFAULT
     */
    public void determineSubjectType() {
        if (hasInfantSpecificData()) {
            this.subjectType = CareSubjectType.INFANT;
        } else {
            this.subjectType = CareSubjectType.DEFAULT;
        }
    }

    /**
     * 신생아 전용 데이터 존재 여부 확인
     */
    private boolean hasInfantSpecificData() {
        return birthWeightGrams != null
                || birthHeightCm != null
                || headCircumferenceCm != null
                || gestationalAgeWeeks != null
                || deliveryType != null
                || allergies != null
                || specialCareNeeds != null
                || lastCheckupDate != null;
    }

    /**
     * 신생아 전용 필드 설정
     */
    public void setInfantData(Integer birthWeightGrams, Integer birthHeightCm,
                              Integer headCircumferenceCm, Integer gestationalAgeWeeks,
                              String deliveryType, String allergies,
                              String specialCareNeeds, LocalDate lastCheckupDate) {
        this.birthWeightGrams = birthWeightGrams;
        this.birthHeightCm = birthHeightCm;
        this.headCircumferenceCm = headCircumferenceCm;
        this.gestationalAgeWeeks = gestationalAgeWeeks;
        this.deliveryType = deliveryType;
        this.allergies = allergies;
        this.specialCareNeeds = specialCareNeeds;
        this.lastCheckupDate = lastCheckupDate;

        // 자동으로 타입 결정
        determineSubjectType();
    }

    /**
     * 임시: mainCaregiver를 createdBy와 동일하게 설정하는 헬퍼 메서드
     */
    public void syncMainCaregiverWithCreatedBy() {
        this.mainCaregiver = this.createdBy;
    }
}
