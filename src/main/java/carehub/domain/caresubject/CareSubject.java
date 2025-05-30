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
        @Index(name = "idx_care_subject_active", columnList = "is_active")
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
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(name = "birth_weight", precision = 5, scale = 2)
    private BigDecimal birthWeight;

    @Column(name = "birth_height", precision = 5, scale = 2)
    private BigDecimal birthHeight;

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
}
