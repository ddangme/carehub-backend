package carehub.web.dto.guardian;

import carehub.domain.guardian.Guardian;
import carehub.domain.guardian.GuardianRole;
import carehub.domain.guardian.GuardianStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardianResponse {
    private Long id;

    // 사용자 정보
    private Long userId;
    private String userName;
    private String userEmail;
    private String userProfileImageUrl;

    // 케어 대상 정보
    private Long careSubjectId;
    private String careSubjectName;

    // 보호자 정보
    private GuardianRole role;
    private GuardianStatus status;
    private Map<String, Boolean> permissions;

    // 초대 정보
    private String invitedByName;
    private String invitedByEmail;
    private LocalDateTime invitedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime rejectedAt;
    private String memo;

    public static GuardianResponse from(Guardian guardian) {
        return GuardianResponse.builder()
                .id(guardian.getId())
                .userId(guardian.getUser().getId())
                .userName(guardian.getUser().getName())
                .userEmail(guardian.getUser().getEmail())
                .userProfileImageUrl(guardian.getUser().getProfileImageUrl())
                .careSubjectId(guardian.getCareSubject().getId())
                .careSubjectName(guardian.getCareSubject().getName())
                .role(guardian.getRole())
                .status(guardian.getStatus())
                .permissions(guardian.getPermissions())
                .invitedByName(guardian.getInvitedBy() != null ? guardian.getInvitedBy().getName() : null)
                .invitedByEmail(guardian.getInvitedBy() != null ? guardian.getInvitedBy().getEmail() : null)
                .invitedAt(guardian.getInvitedAt())
                .acceptedAt(guardian.getAcceptedAt())
                .rejectedAt(guardian.getRejectedAt())
                .memo(guardian.getMemo())
                .build();
    }
}
