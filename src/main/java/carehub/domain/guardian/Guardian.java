package carehub.domain.guardian;

import carehub.domain.caresubject.CareSubject;
import carehub.domain.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "guardians",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "care_subject_id"}),
        indexes = {
                @Index(name = "idx_guardian_user", columnList = "user_id"),
                @Index(name = "idx_guardian_care_subject", columnList = "care_subject_id"),
                @Index(name = "idx_guardian_status", columnList = "status")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_subject_id", nullable = false)
    @JsonBackReference
    private CareSubject careSubject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GuardianRole role;

    @Type(JsonType.class)
    @Column(name = "permissions", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Boolean> permissions = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private GuardianStatus status = GuardianStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;

    @CreationTimestamp
    @Column(name = "invited_at", nullable = false, updatable = false)
    private LocalDateTime invitedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "memo", length = 500)
    private String memo;

    public void acceptInvitation() {
        this.status = GuardianStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void rejectInvitation() {
        this.status = GuardianStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
    }

    public void setDefaultPermissions() {
        permissions.clear();

        switch (role) {
            case PRIMARY:
                permissions.put(Permission.MANAGE_GUARDIANS.getKey(), true);
                permissions.put(Permission.DELETE_CARE_SUBJECT.getKey(), true);
                permissions.put(Permission.UPDATE_CARE_SUBJECT.getKey(), true);
                permissions.put(Permission.RECORD_ACTIVITIES.getKey(), true);
                permissions.put(Permission.VIEW_ALL_ACTIVITIES.getKey(), true);
                permissions.put(Permission.MANAGE_SCHEDULE.getKey(), true);
                permissions.put(Permission.MANAGE_HEALTH_DATA.getKey(), true);
                permissions.put(Permission.MANAGE_DOCUMENTS.getKey(), true);
                break;

            case SECONDARY:
                permissions.put(Permission.MANAGE_GUARDIANS.getKey(), false);
                permissions.put(Permission.DELETE_CARE_SUBJECT.getKey(), false);
                permissions.put(Permission.UPDATE_CARE_SUBJECT.getKey(), true);
                permissions.put(Permission.RECORD_ACTIVITIES.getKey(), true);
                permissions.put(Permission.VIEW_ALL_ACTIVITIES.getKey(), true);
                permissions.put(Permission.MANAGE_SCHEDULE.getKey(), true);
                permissions.put(Permission.MANAGE_HEALTH_DATA.getKey(), true);
                permissions.put(Permission.MANAGE_DOCUMENTS.getKey(), true);
                break;

            case TEMPORARY:
                // 임시 보호자는 기본 기록 권한만
                permissions.put(Permission.MANAGE_GUARDIANS.getKey(), false);
                permissions.put(Permission.DELETE_CARE_SUBJECT.getKey(), false);
                permissions.put(Permission.UPDATE_CARE_SUBJECT.getKey(), false);
                permissions.put(Permission.RECORD_ACTIVITIES.getKey(), true);
                permissions.put(Permission.VIEW_ALL_ACTIVITIES.getKey(), true);
                permissions.put(Permission.MANAGE_SCHEDULE.getKey(), false);
                permissions.put(Permission.MANAGE_HEALTH_DATA.getKey(), false);
                permissions.put(Permission.MANAGE_DOCUMENTS.getKey(), false);
                break;
        }
    }

    public boolean hasPermission(String permission) {
        return permissions.getOrDefault(permission, false);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.getOrDefault(permission.getKey(), false);
    }
}
