package carehub.web.dto.guardian;

import carehub.domain.guardian.GuardianRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 보호자 초대 요청 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardianInviteRequest {

    @NotNull(message = "케어 대상 ID는 필수입니다")
    private Long careSubjectId;

    @NotNull(message = "초대할 사용자 이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String inviteeEmail;

    @NotNull(message = "보호자 역할은 필수입니다")
    private GuardianRole role;

    private String memo;
}
